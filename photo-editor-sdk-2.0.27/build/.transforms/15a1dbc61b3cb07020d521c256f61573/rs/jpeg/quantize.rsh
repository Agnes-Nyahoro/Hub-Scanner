
//rs_allocation rsAllocationQY;
//rs_allocation rsAllocationQCb;
//rs_allocation rsAllocationQCr;

rs_allocation rsAllocationQ;

static int quantumLuminance[64];
static int quantumChrominance[64];
static double divisorsLuminance[64];
static double divisorsChrominance[64];

// Call it only one time!
void initMatrix(int quality) {
    // converting quality setting
    // method in the IJG Jpeg-6a C libraries

    quality = clampInt(quality, 1, 100);
    quality = (quality < 50) ? (quality = 5000 / quality) : (200 - quality * 2);

    for (int i = 0; i < 64; i++) {
        quantumLuminance[i] = clampInt((baseQuantumLuminance[i] * quality + 50) / 100, 1, 255);
    }
    int index = 0;
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            divisorsLuminance[index] = 1.0 / (8.0 * quantumLuminance[index] * AANScaleFactor[i] * AANScaleFactor[j]);
            index++;
        }
    }

    for (int i = 0; i < 64; i++) {
        quantumChrominance[i] = clampInt((baseQuantumChrominance[i] * quality + 50) / 100, 1, 255);
    }

    index = 0;
    for (int i = 0; i < 8; i++) {
        for (int j = 0; j < 8; j++) {
            divisorsChrominance[index] = 1.0 / (8.0 * quantumChrominance[index] * AANScaleFactor[i] * AANScaleFactor[j]);
            index++;
        }
    }
}

static inline void quantizeBlock(double3 inputData[64], int3 *outputData) {
    uchar index = 0;
    for (uchar i = 0; i < 8; i++) for (uchar j = 0; j < 64; j+= 8) {
        double3 input = inputData[i + j];
        int3 output;

        output.x = roundInt((input.x * divisorsLuminance[index]));
        output.yz = roundInt2((input.yz * divisorsChrominance[index]));

        outputData[index] = output;
        index++;
    }
}

static inline void forwardDCTCell(uint32_t x, uint32_t y) {
    double3 output[64];
    double3 tmp0 = 0;
    double3 tmp1 = 0;
    double3 tmp2 = 0;
    double3 tmp3 = 0;
    double3 tmp4 = 0;
    double3 tmp5 = 0;
    double3 tmp6 = 0;
    double3 tmp7 = 0;

    double3 tmp10 = 0;
    double3 tmp11 = 0;
    double3 tmp12 = 0;
    double3 tmp13 = 0;
    double3 z1 = 0;
    double3 z2 = 0;
    double3 z3 = 0;
    double3 z4 = 0;
    double3 z5 = 0;
    double3 z11 = 0;
    double3 z13 = 0;

    uchar i;
    uchar j;

    int index = 0;
    for (i = 0; i < 8; i++) for (j = 0; j < 8; j++) {
        float3 value = rsGetElementAt_float3(rsAllocationYUV, x + i, y + j) - 128.0;
        double3 dValue = { value.x, value.y, value.z };
        output[index] = dValue;
        index++;
    }

    for (i = 0; i < 8; i++) {
        tmp0 = output[i     ] + output[i + 56];
        tmp7 = output[i     ] - output[i + 56];
        tmp1 = output[i +  8] + output[i + 48];
        tmp6 = output[i +  8] - output[i + 48];
        tmp2 = output[i + 16] + output[i + 40];
        tmp5 = output[i + 16] - output[i + 40];
        tmp3 = output[i + 24] + output[i + 32];
        tmp4 = output[i + 24] - output[i + 32];

        tmp10 = tmp0 + tmp3;
        tmp13 = tmp0 - tmp3;
        tmp11 = tmp1 + tmp2;
        tmp12 = tmp1 - tmp2;

        output[i     ] = tmp10 + tmp11;
        output[i + 32] = tmp10 - tmp11;

        z1 = (tmp12 + tmp13) * 0.707106781;
        output[i + 16] = tmp13 + z1;
        output[i + 48] = tmp13 - z1;

        tmp10 = tmp4 + tmp5;
        tmp11 = tmp5 + tmp6;
        tmp12 = tmp6 + tmp7;

        z5 = (tmp10 - tmp12) * 0.382683433;
        z2 = 0.541196100 * tmp10 + z5;
        z4 = 1.306562965 * tmp12 + z5;
        z3 = tmp11 * 0.707106781;

        z11 = tmp7 + z3;
        z13 = tmp7 - z3;

        output[i + 40] = z13 + z2;
        output[i + 24] = z13 - z2;
        output[i +  8] = z11 + z4;
        output[i + 56] = z11 - z4;
    }

    for (i = 0; i < 64; i += 8) {
        tmp0 = output[    i] + output[7 + i];
        tmp7 = output[    i] - output[7 + i];
        tmp1 = output[1 + i] + output[6 + i];
        tmp6 = output[1 + i] - output[6 + i];
        tmp2 = output[2 + i] + output[5 + i];
        tmp5 = output[2 + i] - output[5 + i];
        tmp3 = output[3 + i] + output[4 + i];
        tmp4 = output[3 + i] - output[4 + i];

        tmp10 = tmp0 + tmp3;
        tmp13 = tmp0 - tmp3;
        tmp11 = tmp1 + tmp2;
        tmp12 = tmp1 - tmp2;

        output[    i] = tmp10 + tmp11;
        output[4 + i] = tmp10 - tmp11;

        z1 = (tmp12 + tmp13) * 0.707106781;
        output[2 + i] = tmp13 + z1;
        output[6 + i] = tmp13 - z1;

        tmp10 = tmp4 + tmp5;
        tmp11 = tmp5 + tmp6;
        tmp12 = tmp6 + tmp7;

        z5 = (tmp10 - tmp12) * 0.382683433;
        z2 = 0.541196100 * tmp10 + z5;
        z4 = 1.306562965 * tmp12 + z5;
        z3 = tmp11 * 0.707106781;

        z11 = tmp7 + z3;
        z13 = tmp7 - z3;

        output[5 + i] = z13 + z2;
        output[3 + i] = z13 - z2;
        output[1 + i] = z11 + z4;
        output[7 + i] = z11 - z4;
    }

    int3 result[64] = {}; quantizeBlock(output, (int3*) &result);

    uint2 pos; pos.x = (x << 3); pos.y = (y >> 3);
    for (i = 0; i < 64; i++) {
        //rsSetElementAt_int(rsAllocationQY,  result[i].x, pos.x + i, pos.y);
        //rsSetElementAt_int(rsAllocationQCb, result[i].y, pos.x + i, pos.y);
        //rsSetElementAt_int(rsAllocationQCr, result[i].z, pos.x + i, pos.y);
        rsSetElementAt_int3(rsAllocationQ, result[i], pos.x + i, pos.y);
    }

    //index = 0;
    //for (j = 0; j < 8; j++) for (i = 0; i < 8; i++) {
    //    rsSetElementAt_int(*rsAllocationOut, result[index], x + i, y + j);
    //    index++;
    //}
}

void __attribute__((kernel)) forwardDCT(float3 dummy, uint32_t x, uint32_t y) {
    if ((x >> 3) << 3 == x && (y >> 3) << 3 == y) {
        forwardDCTCell(x, y);
    }
}