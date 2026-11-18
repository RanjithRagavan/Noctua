#include "NoctuaAgentService.h"

#include <android/log.h>
#include <binder/Status.h>
#include <executorch/extension/tensor/tensor.h>
#include <executorch/runtime/core/error.h>

#include <algorithm>
#include <chrono>
#include <cmath>

namespace com::noctua::aosp {
namespace {

using ::android::binder::Status;
using ::executorch::extension::make_tensor_ptr;
using ::executorch::runtime::Error;

constexpr char kLogTag[] = "NoctuaAgent";
constexpr char kRuntimeName[] = "ExecuTorch AOSP / portable CPU";

bool isValidFeatureVector(const std::vector<float>& features) {
    return features.size() == NoctuaAgentService::kFeatureCount &&
            std::all_of(features.begin(), features.end(), [](float value) {
                // Noctua's WellnessFeatures.toVector() clamps to [-1, 1].
                return std::isfinite(value) && value >= -1.0f && value <= 1.0f;
            });
}

}  // namespace

NoctuaAgentService::NoctuaAgentService(std::string modelPath)
      : module_(std::move(modelPath)) {}

::android::status_t NoctuaAgentService::init() {
    const Error error = module_.load_forward();
    if (error != Error::Ok) {
        __android_log_print(ANDROID_LOG_ERROR, kLogTag,
                            "Failed to load ExecuTorch forward method: 0x%x",
                            static_cast<unsigned>(error));
        return ::android::UNKNOWN_ERROR;
    }
    return ::android::OK;
}

Status NoctuaAgentService::forecast(const std::vector<float>& features,
                                    ForecastResult* result) {
    if (result == nullptr) {
        return Status::fromExceptionCode(Status::EX_NULL_POINTER, "result is null");
    }
    if (!isValidFeatureVector(features)) {
        return Status::fromExceptionCode(
                Status::EX_ILLEGAL_ARGUMENT,
                "Expected 8 finite, normalized floats in [-1, 1]");
    }

    const auto startedAt = std::chrono::steady_clock::now();
    std::lock_guard<std::mutex> lock(mutex_);

    // Owning TensorPtr keeps the input and metadata alive through forward().
    auto input = make_tensor_ptr({1, static_cast<int>(kFeatureCount)}, features);
    const auto outputs = module_.forward(input);
    if (!outputs.ok()) {
        __android_log_print(ANDROID_LOG_ERROR, kLogTag,
                            "ExecuTorch forward failed: 0x%x",
                            static_cast<unsigned>(outputs.error()));
        return Status::fromExceptionCode(Status::EX_ILLEGAL_STATE,
                                         "ExecuTorch forward failed");
    }
    if (outputs->empty() || !outputs->at(0).isTensor() ||
        outputs->at(0).toTensor().numel() < 1) {
        return Status::fromExceptionCode(Status::EX_ILLEGAL_STATE,
                                         "Model returned no scalar tensor");
    }

    const float rawScore = outputs->at(0).toTensor().const_data_ptr<float>()[0];
    if (!std::isfinite(rawScore)) {
        return Status::fromExceptionCode(Status::EX_ILLEGAL_STATE,
                                         "Model returned a non-finite score");
    }

    const auto elapsed = std::chrono::duration_cast<std::chrono::microseconds>(
            std::chrono::steady_clock::now() - startedAt);
    result->rawScore = rawScore;
    result->readinessScore = static_cast<int32_t>(std::clamp(rawScore, 0.0f, 100.0f));
    result->inferenceMicros = static_cast<int64_t>(elapsed.count());
    result->runtime = kRuntimeName;
    return Status::ok();
}

Status NoctuaAgentService::getRuntimeInfo(std::string* result) {
    if (result == nullptr) {
        return Status::fromExceptionCode(Status::EX_NULL_POINTER, "result is null");
    }
    *result = kRuntimeName;
    return Status::ok();
}

}  // namespace com::noctua::aosp
