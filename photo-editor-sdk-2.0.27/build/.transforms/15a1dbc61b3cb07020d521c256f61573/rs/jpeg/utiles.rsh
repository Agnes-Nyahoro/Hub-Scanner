
static inline int clampInt(int value, int from, int to) {
    return value > from ? value < to ? value : to : from;
}

static inline int roundInt(double value) {
    return (int) ((value < 0.0) ? (value - 0.5) : (value + 0.5));
}

static inline int2 roundInt2(double2 value) {
    int2 result;
    result.x = (int) ((value.x < 0.0) ? (value.x - 0.5) : (value.x + 0.5));
    result.y = (int) ((value.y < 0.0) ? (value.y - 0.5) : (value.y + 0.5));
    return result;
}

static inline double3 convertFloat3ToDouble3(float3 value) {
    double3 d_value = {value.x, value.y, value.z};
    return d_value;
}

static uint stringLength(const char *value) {
    uint32_t l = 0;
    while (value[l] != 0x00) l++;
    return l;
}