#include "NoctuaAgentService.h"

#include <android/log.h>
#include <binder/IPCThreadState.h>
#include <binder/IServiceManager.h>
#include <binder/ProcessState.h>
#include <utils/String16.h>

namespace {

constexpr char kServiceName[] = "noctua_agent";
constexpr char kModelPath[] = "/system/etc/noctua/readiness_forecaster.pte";

}  // namespace

int main() {
    using ::com::noctua::aosp::NoctuaAgentService;

    ::android::sp<NoctuaAgentService> service = new NoctuaAgentService(kModelPath);
    if (service->init() != ::android::OK) {
        __android_log_print(ANDROID_LOG_ERROR, "NoctuaAgent",
                            "Model initialization failed; not registering service");
        return 1;
    }

    const ::android::status_t status = ::android::defaultServiceManager()->addService(
            ::android::String16(kServiceName), service);
    if (status != ::android::OK) {
        __android_log_print(ANDROID_LOG_ERROR, "NoctuaAgent",
                            "addService(%s) failed: %d", kServiceName, status);
        return 1;
    }

    __android_log_print(ANDROID_LOG_INFO, "NoctuaAgent",
                        "Registered %s; model loaded from %s", kServiceName, kModelPath);
    ::android::ProcessState::self()->startThreadPool();
    ::android::IPCThreadState::self()->joinThreadPool();
    return 0;
}
