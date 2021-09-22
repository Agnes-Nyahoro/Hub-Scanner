
rs_allocation rsAllocationWriteBuffer;

static uint byteCount = 0;
static void write(uchar value) {
    rsSetElementAt_uchar(rsAllocationWriteBuffer, value, byteCount++);
}

static void writeString(const char *value) {
    for (uint32_t s = 0, l = stringLength(value); s < l; s++) {
        write((uchar) value[s]);
    }
}

static void writeArray(uchar *value, uint32_t count) {
    for (uint32_t b = 0; b < count; b++) {
        write((uchar) value[b]);
    }
}

static void writeHeaderBlock(uchar *block) {
    uint32_t v1 = block[2];
    uint32_t v2 = block[3];

    v1 <<= 8;

    uint32_t count = v1 + v2 + 2;
    writeArray(block, count);
}

static void writeMarker(uchar data[2]) {
    writeArray(data, 2);
}

void flushBuffer() {
    rsSetElementAt_int(rsAllocationInfoStore, (int) byteCount, 0);
    byteCount = 0;
}