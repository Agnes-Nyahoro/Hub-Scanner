
static int dc_matrix[2][12][2];
static int ac_matrix[2][255][2];

static int bufferPutBits;
static int bufferPutBuffer;

void initHuf() {
    int p, l, i, lastp, si, code;
    int huffsize[257];
    int huffcode[257];

    byteCount = 0;

    /*
    * init of the DC values for the chrominance
    * [][0] is the code   [][1] is the number of bit
    */
    p = 0;
    for (l = 1; l <= 16; l++) {
        for (i = 1; i <= bitsDCchrominance[l]; i++) {
            huffsize[p++] = l;
        }
    }
    huffsize[p] = 0;
    lastp = p;

    code = 0;
    si = huffsize[0];
    p = 0;
    while (huffsize[p] != 0) {
        while (huffsize[p] == si) {
            huffcode[p++] = code;
            code++;
        }
        code <<= 1;
        si++;
    }

    for (p = 0; p < lastp; p++) {
        dc_matrix[1][valDCchrominance[p]][0] = huffcode[p];
        dc_matrix[1][valDCchrominance[p]][1] = huffsize[p];
    }

    /*
    * Init of the AC hufmann code for the chrominance
    * matrix [][][0] is the code & matrix[][][1] is the number of bit needed
    */
    p = 0;
    for (l = 1; l <= 16; l++) {
        for (i = 1; i <= bitsACchrominance[l]; i++) {
            huffsize[p++] = l;
        }
    }
    huffsize[p] = 0;
    lastp = p;

    code = 0;
    si = huffsize[0];
    p = 0;
    while (huffsize[p] != 0) {
        while (huffsize[p] == si) {
            huffcode[p++] = code;
            code++;
        }
        code <<= 1;
        si++;
    }

    for (p = 0; p < lastp; p++) {
        ac_matrix[1][valACchrominance[p]][0] = huffcode[p];
        ac_matrix[1][valACchrominance[p]][1] = huffsize[p];
    }

    /*
    * init of the DC values for the luminance
    * [][0] is the code   [][1] is the number of bit
    */
    p = 0;
    for (l = 1; l <= 16; l++) {
        for (i = 1; i <= bitsDCluminance[l]; i++) {
            huffsize[p++] = l;
        }
    }
    huffsize[p] = 0;
    lastp = p;

    code = 0;
    si = huffsize[0];
    p = 0;
    while (huffsize[p] != 0) {
        while (huffsize[p] == si) {
            huffcode[p++] = code;
            code++;
        }
        code <<= 1;
        si++;
    }

    for (p = 0; p < lastp; p++) {
        dc_matrix[0][valDCluminance[p]][0] = huffcode[p];
        dc_matrix[0][valDCluminance[p]][1] = huffsize[p];
    }

    /*
    * Init of the AC hufmann code for luminance
    * matrix [][][0] is the code & matrix[][][1] is the number of bit
    */

    p = 0;
    for (l = 1; l <= 16; l++) {
        for (i = 1; i <= bitsACluminance[l]; i++) {
            huffsize[p++] = l;
        }
    }
    huffsize[p] = 0;
    lastp = p;

    code = 0;
    si = huffsize[0];
    p = 0;
    while (huffsize[p] != 0) {
        while (huffsize[p] == si) {
            huffcode[p++] = code;
            code++;
        }
        code <<= 1;
        si++;
    }
    for (int q = 0; q < lastp; q++) {
        ac_matrix[0][valACluminance[q]][0] = huffcode[q];
        ac_matrix[0][valACluminance[q]][1] = huffsize[q];
    }
}

void bufferIt(int code, int size) {
    int putBuffer = code;
    int putBits = bufferPutBits;
    putBuffer &= (1 << size) - 1;
    putBits += size;
    putBuffer <<= 24 - putBits;
    putBuffer |= bufferPutBuffer;
    while (putBits >= 8) {
        int c = ((putBuffer >> 16) & 0xFF);
        write((uchar)c);
        if (c == 0xFF) {
            write((uchar)0);
        }
        putBuffer <<= 8;
        putBits -= 8;
    }

    bufferPutBuffer = putBuffer;
    bufferPutBits = putBits;
}

static void huffmanBlockEncoder(int zigzag[64], int prec, int DCcode, int ACcode) {
    int temp, temp2, nbits, k, r, i;

    // The DC portion
    temp = temp2 = zigzag[0] - prec;
    if (temp < 0) {
        temp = -temp;
        temp2--;
    }

    nbits = 0;
    while (temp != 0) {
        nbits++;
        temp >>= 1;
    }

    // if (nbits > 11) nbits = 11;
    bufferIt(dc_matrix[DCcode][nbits][0], dc_matrix[DCcode][nbits][1]);
    // The arguments in bufferIt are code and size.
    if (nbits != 0) {
        bufferIt(temp2, nbits);
    }

    // The AC portion
    r = 0;
    for (k = 1; k < 64; k++) {
        if ((temp = zigzag[jpegNaturalOrder[k]]) == 0) {
            r++;
        } else {
            while (r > 15) {
                bufferIt(ac_matrix[ACcode][0xF0][0], ac_matrix[ACcode][0xF0][1]);
                r -= 16;
            }
            temp2 = temp;
            if (temp < 0) {
                temp = -temp;
                temp2--;
            }
            nbits = 1;
            while ((temp >>= 1) != 0) {
                nbits++;
            }
            i = (r << 4) + nbits;
            bufferIt(ac_matrix[ACcode][i][0], ac_matrix[ACcode][i][1]);
            bufferIt(temp2, nbits);

            r = 0;
        }
    }

    if (r > 0) {
        bufferIt(ac_matrix[ACcode][0][0], ac_matrix[ACcode][0][1]);
    }
}

/*
* Initialisation of the Huffman codes for Luminance and Chrominance.
* This code results in the same tables created in the IJG Jpeg-6a
* library.
*/
static void flushHuffmanBuffer() {
    int putBuffer = bufferPutBuffer;
    int putBits = bufferPutBits;
    while (putBits >= 8) {
        int c = ((putBuffer >> 16) & 0xFF);
        write((uchar)c);
        if (c == 0xFF) {
            write((uchar)0);
        }
        putBuffer <<= 8;
        putBits -= 8;
    }
    if (putBits > 0) {
        int c = ((putBuffer >> 16) & 0xFF);
        write((uchar)c);
    }
    rsSetElementAt_int(rsAllocationInfoStore, (int) byteCount, 0);
}

void writeEOI() {
    flushHuffmanBuffer();
    uchar EOI[] = {(uchar) 0xFF, (uchar) 0xD9};
    writeMarker(EOI);
}

static int3 lastDCvalue = {0, 0, 0};
void huffmanEncode(uint32_t w, uint32_t h) {
    int blockBufferX[64];
    int blockBufferY[64];
    int blockBufferZ[64];

    for (uint32_t y = 0; y < h; y++) {
        for (uint32_t x = 0; x < w; x += 64) {

            for (uchar i = 0; i < 64; i++) {
                uint32_t posX = x + i;
                int3 value = rsGetElementAt_int3(rsAllocationQ,  posX, y);
                 blockBufferX[i] = value.x;
                 blockBufferY[i] = value.y;
                 blockBufferZ[i] = value.z;

                //blockBufferX[i] = rsGetElementAt_int(rsAllocationQY,  posX, y);
                //blockBufferY[i] = rsGetElementAt_int(rsAllocationQCb, posX, y);
                //blockBufferZ[i] = rsGetElementAt_int(rsAllocationQCr, posX, y);
            }

            huffmanBlockEncoder(blockBufferX, lastDCvalue.x, 0, 0);
            huffmanBlockEncoder(blockBufferY, lastDCvalue.y, 1, 1);
            huffmanBlockEncoder(blockBufferZ, lastDCvalue.z, 1, 1);

            lastDCvalue.x = blockBufferX[0];
            lastDCvalue.y = blockBufferY[0];
            lastDCvalue.z = blockBufferZ[0];
        }
    }
}