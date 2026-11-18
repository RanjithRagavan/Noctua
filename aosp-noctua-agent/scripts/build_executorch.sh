#!/usr/bin/env bash
# Cross-compile the small C++ runtime surface needed by the AOSP demo.
# Usage: EXECUTORCH_ROOT=/path/to/executorch ANDROID_NDK_ROOT=/path/to/ndk \
#          ./scripts/build_executorch.sh [arm64-v8a x86_64]
set -euo pipefail

DEMO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
: "${EXECUTORCH_ROOT:?Set EXECUTORCH_ROOT to an ExecuTorch 1.4.1 source checkout}"
: "${ANDROID_NDK_ROOT:?Set ANDROID_NDK_ROOT to an Android NDK installation}"

if [[ "$(basename "$EXECUTORCH_ROOT")" != "executorch" ]]; then
    echo "ExecuTorch 1.4.1 requires its source directory to be named 'executorch'." >&2
    exit 2
fi

ABIS=("$@")
if [[ ${#ABIS[@]} -eq 0 ]]; then
    ABIS=(arm64-v8a)
fi

for ABI in "${ABIS[@]}"; do
    case "$ABI" in
        arm64-v8a) OUT_ARCH=arm64 ;;
        x86_64) OUT_ARCH=x86_64 ;;
        *) echo "Unsupported ABI: $ABI (use arm64-v8a or x86_64)" >&2; exit 2 ;;
    esac

    BUILD_DIR="$DEMO_ROOT/out/executorch-$ABI"
    INSTALL_DIR="$DEMO_ROOT/out/install-$ABI"
    cmake -S "$EXECUTORCH_ROOT" -B "$BUILD_DIR" \
        -DCMAKE_TOOLCHAIN_FILE="$ANDROID_NDK_ROOT/build/cmake/android.toolchain.cmake" \
        -DANDROID_ABI="$ABI" \
        -DANDROID_PLATFORM=30 \
        -DANDROID_STL=c++_static \
        -DCMAKE_BUILD_TYPE=Release \
        -DEXECUTORCH_BUILD_SHARED=ON \
        -DEXECUTORCH_BUILD_PORTABLE_OPS=ON \
        -DEXECUTORCH_BUILD_KERNELS_OPTIMIZED=OFF \
        -DEXECUTORCH_BUILD_XNNPACK=OFF \
        -DEXECUTORCH_BUILD_EXTENSION_MODULE=ON \
        -DEXECUTORCH_BUILD_EXTENSION_TENSOR=ON \
        -DEXECUTORCH_BUILD_EXTENSION_DATA_LOADER=ON \
        -DEXECUTORCH_BUILD_EXTENSION_FLAT_TENSOR=ON \
        -DEXECUTORCH_BUILD_EXTENSION_NAMED_DATA_MAP=ON \
        -DEXECUTORCH_BUILD_EXECUTOR_RUNNER=OFF \
        -DEXECUTORCH_BUILD_TESTS=OFF
    cmake --build "$BUILD_DIR" --parallel
    cmake --install "$BUILD_DIR" --prefix "$INSTALL_DIR"

    mkdir -p "$DEMO_ROOT/prebuilts/$OUT_ARCH" "$DEMO_ROOT/prebuilts/include"
    cp "$INSTALL_DIR/lib/libexecutorch.so" "$DEMO_ROOT/prebuilts/$OUT_ARCH/"
    cp "$INSTALL_DIR/lib/libexecutorch_portable_ops.so" "$DEMO_ROOT/prebuilts/$OUT_ARCH/"
    rm -rf "$DEMO_ROOT/prebuilts/include"
    cp -R "$INSTALL_DIR/include" "$DEMO_ROOT/prebuilts/include"
done

echo "Prebuilts written under $DEMO_ROOT/prebuilts"
