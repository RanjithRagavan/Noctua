#pragma once

#include <com/noctua/aosp/BnNoctuaAgent.h>
#include <executorch/extension/module/module.h>
#include <utils/Errors.h>

#include <mutex>
#include <string>
#include <vector>

namespace com::noctua::aosp {

class NoctuaAgentService final : public BnNoctuaAgent {
public:
    static constexpr size_t kFeatureCount = 8;

    explicit NoctuaAgentService(std::string modelPath);

    // Loads the program and forward method before the service is published.
    ::android::status_t init();

    ::android::binder::Status forecast(const std::vector<float>& features,
                                       ForecastResult* result) override;
    ::android::binder::Status getRuntimeInfo(std::string* result) override;

private:
    // Module::forward() is serialized so concurrent Binder threads cannot
    // mutate the same method's input/output state at the same time.
    std::mutex mutex_;
    ::executorch::extension::Module module_;
};

}  // namespace com::noctua::aosp
