# Include from the device/product configuration after copying this demo into
# the AOSP tree. Fix the path if you use a location other than vendor/noctua.
PRODUCT_PACKAGES += \
    noctua_agent \
    noctua_client \
    noctua_model

BOARD_SEPOLICY_DIRS += vendor/noctua/aosp-noctua-agent/sepolicy
