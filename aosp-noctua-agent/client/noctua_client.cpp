#include <binder/IServiceManager.h>
#include <com/noctua/aosp/INoctuaAgent.h>
#include <utils/String16.h>

#include <cstdio>
#include <cstdlib>
#include <string>
#include <vector>

namespace {

constexpr char kServiceName[] = "noctua_agent";

}  // namespace

int main(int argc, char** argv) {
    using ::com::noctua::aosp::ForecastResult;
    using ::com::noctua::aosp::INoctuaAgent;

    std::vector<float> features(8, 0.0f);
    if (argc == 9) {
        for (int i = 0; i < 8; ++i) {
            features[i] = std::strtof(argv[i + 1], nullptr);
        }
    } else if (argc != 1) {
        std::fprintf(stderr,
                     "Usage: noctua_client [sleepDebt hrvZ trend readinessMean "
                     "sleepMean temperature efficiency steps]\n"
                     "All eight arguments are normalized floats in [-1, 1].\n");
        return 2;
    }

    ::android::sp<INoctuaAgent> service =
            ::android::waitForService<INoctuaAgent>(::android::String16(kServiceName));
    if (service == nullptr) {
        std::fprintf(stderr, "Service %s is unavailable\n", kServiceName);
        return 1;
    }

    ForecastResult result;
    const auto status = service->forecast(features, &result);
    if (!status.isOk()) {
        std::fprintf(stderr, "forecast failed: %s\n", status.toString8().c_str());
        return 1;
    }

    std::printf("readiness=%d raw=%.3f inference=%lld us runtime=%s\n",
                result.readinessScore, result.rawScore,
                static_cast<long long>(result.inferenceMicros), result.runtime.c_str());
    return 0;
}
