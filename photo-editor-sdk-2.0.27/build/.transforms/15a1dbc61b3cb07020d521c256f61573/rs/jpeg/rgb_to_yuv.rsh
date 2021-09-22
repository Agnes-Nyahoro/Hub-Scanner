
rs_allocation rsAllocationYUV;

void __attribute__((kernel)) root(uchar4 sourceColor, uint32_t x, uint32_t y) {
    uchar r = sourceColor.r;
    uchar g = sourceColor.g;
    uchar b = sourceColor.b;

    float3 result = {
        ((r * 0.299f)    + (g * 0.587f)   + (b * 0.114f)  ),
        ((r * -0.16874f) - (g * 0.33126f) + (b >> 1)      ) + 128,
        ((r >> 1)        - (g * 0.41869f) - (b * 0.08131f)) + 128
    };

    rsSetElementAt_float3(rsAllocationYUV,  result, x, y);
}