package com.noctua.aosp;

import com.noctua.aosp.ForecastResult;

/** Platform-local inference API. The input is Noctua's normalized [1, 8] vector. */
interface INoctuaAgent {
    ForecastResult forecast(in float[] features);
    @utf8InCpp String getRuntimeInfo();
}
