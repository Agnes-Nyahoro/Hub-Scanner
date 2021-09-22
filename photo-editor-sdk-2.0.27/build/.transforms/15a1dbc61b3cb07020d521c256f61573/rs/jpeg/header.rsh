static inline void writeDHTHeader() {
    uint32_t index = 4;
    uint32_t bytes = 0;

    uchar DHT[1024];
    DHT[0] = 0xFF;
    DHT[1] = 0xC4;

    DHT[index++] = bitsDCluminance[0];
    for (uint32_t a = 1; a < 17; a++) {
        uchar value = bitsDCluminance[a];
        DHT[index++] = value;
        bytes += value;
    }
    for (uint32_t b = 0; b < bytes; b++) {
        DHT[index++] = valDCluminance[b];
    }

    bytes = 0;
    DHT[index++] = bitsACluminance[0];
    for (uint32_t c = 1; c < 17; c++) {
        uchar value = bitsACluminance[c];
        DHT[index++] = value;
        bytes += value;
    }
    for (uint32_t d = 0; d < bytes; d++) {
        DHT[index++] = valACluminance[d];
    }

    bytes = 0;
    DHT[index++] = bitsDCchrominance[0];
    for (uint32_t e = 1; e < 17; e++) {
        uchar value = bitsDCchrominance[e];
        DHT[index++] = value;
        bytes += value;
    }
    for (uint32_t f = 0; f < bytes; f++) {
        DHT[index++] = valDCchrominance[f];
    }

    bytes = 0;
    DHT[index++] = bitsACchrominance[0];
    for (uint32_t g = 1; g < 17; g++) {
        uchar value = bitsACchrominance[g];
        DHT[index++] = value;
        bytes += value;
    }
    for (uint32_t h = 0; h < bytes; h++) {
        DHT[index++] = valACchrominance[h];
    }
    DHT[2] = (uchar) (((index - 2) >> 8) & 0xFF);
    DHT[3] = (uchar) ((index - 2) & 0xFF);

    //writeArray(DHT, index);
    writeHeaderBlock(DHT);
}

void writeHeaders(uint imageWidth, uint imageHeight) {

    // https://de.wikipedia.org/wiki/JPEG_File_Interchange_Format

    uint32_t i;
    uint32_t j;
    uint32_t index;

    // SOI marker
    uchar SOI[] = {0xFF, 0xD8};
    writeMarker(SOI);

    // JFIF Tag
    uchar JFIF[18] = { 0 };
    JFIF[0] = 0xff;
    JFIF[1] = 0xe0;
    JFIF[2] = 0x00;
    JFIF[3] = 16;
    JFIF[4] = 0x4a;
    JFIF[5] = 0x46;
    JFIF[6] = 0x49;
    JFIF[7] = 0x46;
    JFIF[8] = 0x00;
    JFIF[9] = 0x01;
    JFIF[10] = 0x00;
    JFIF[11] = 0x00;
    JFIF[12] = 0x00;
    JFIF[13] = 0x01;
    JFIF[14] = 0x00;
    JFIF[15] = 0x01;
    JFIF[16] = 0x00;
    JFIF[17] = 0x00;
    writeHeaderBlock(JFIF);

    // JPEG Comment
    uint count = stringLength(ENCODER_COMMENT) + 2;
    uchar COM[4] = { 0 };
    COM[0] = 0xFF;
    COM[1] = 0xFE;
    COM[2] = ((count >> 8) & 0xFF);
    COM[3] = ((count     ) & 0xFF);

    writeArray(COM, 4);
    writeString(ENCODER_COMMENT);

    // The DQT header
    // 0 is the luminance index and 1 is the chrominance index
    uchar DQT[134] = { 0 };
    index = 0;
    DQT[index++] = 0xFF;
    DQT[index++] = 0xDB;
    DQT[index++] = 0x00;
    DQT[index++] = 132;
    DQT[index++] = 0;
    for (j = 0; j < 64; j++) {
        DQT[index++] = quantumLuminance[jpegNaturalOrder[j]];
    }
    DQT[index++] = 1;
    for (j = 0; j < 64; j++) {
        DQT[index++] = quantumChrominance[jpegNaturalOrder[j]];
    }

    writeHeaderBlock(DQT);

    // Start of Frame Header
    uchar SOF[19] = { 0 };
    index = 0;
    SOF[index++] = 0xFF;
    SOF[index++] = 0xC0;
    SOF[index++] = 0x00;
    SOF[index++] = 17;
    SOF[index++] = PRECISION;
    SOF[index++] = ((imageHeight >> 8) & 0xFF);
    SOF[index++] = ((imageHeight) & 0xFF);
    SOF[index++] = ((imageWidth >> 8) & 0xFF);
    SOF[index++] = ((imageWidth) & 0xFF);
    SOF[index++] = NUMBER_OF_COMPONENTS;

    // Y - Component Number
    SOF[index++] = 1;
    SOF[index++] = ((1 << 4) + 1);
    SOF[index++] = 0;

    // U - Component Number
    SOF[index++] = 2;
    SOF[index++] = ((1 << 4) + 1);
    SOF[index++] = 1;

    // V - Component Number
    SOF[index++] = 3;
    SOF[index++] = ((1 << 4) + 1);
    SOF[index++] = 1;

    writeHeaderBlock(SOF);

    // Write the DHT Header
    writeDHTHeader();

    // Start of Scan Header
    uchar SOS[14] = { 0 };
    index = 0;
    SOS[index++] = 0xFF;
    SOS[index++] = 0xDA;
    SOS[index++] = 0x00;
    SOS[index++] = 12;
    SOS[index++] = NUMBER_OF_COMPONENTS;

    // Write Component info
    // Y - Component Number
    SOS[index++] = 1;
    // (Y_DC_TABLE_NUMBER << 4) + Y_AC_TABLE_NUMBER
    SOS[index++] = 0;

    // U - Component Number
    SOS[index++] = 2;
    // (U_DC_TABLE_NUMBER << 4) + U_AC_TABLE_NUMBER
    SOS[index++] = ((1 << 4) + 1);

    // V - Component Number
    SOS[index++] = 3;
    // (V_DC_TABLE_NUMBER << 4) + V_AC_TABLE_NUMBER
    SOS[index++] = ((1 << 4) + 1);

    // Write Block info
    SOS[index++] = Ss;
    SOS[index++] = Se;
    SOS[index++] = 0;//((Ah << 4) + Al);
    writeHeaderBlock(SOS);
}
