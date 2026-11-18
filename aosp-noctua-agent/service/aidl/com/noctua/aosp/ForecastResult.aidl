package com.noctua.aosp;

/** Result of one local readiness inference. No biometric history crosses Binder. */
parcelable ForecastResult {
    int readinessScore;
    float rawScore;
    long inferenceMicros;
    @utf8InCpp String runtime;
}
