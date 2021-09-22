#pragma version(1)
#pragma rs java_package_name(ly.img.android)
#pragma rs_fp_relaxed

#include "rs_graphics.rsh"

rs_allocation rsAllocationIn;
rs_allocation rsAllocationOut;

uchar radius = 0x0;
uint widthidth = 0;
uint height = 0;
void setImageAlpha(uchar4 *v_out, uint32_t x, uint32_t y) {

    uchar4 sourceColor = rsGetElementAt_uchar4(rsAllocationIn,  x, y);
    uchar4 blendColor  = rsGetElementAt_uchar4(rsAllocationOut, x, y);

    uchar r = ((blendColor.r - sourceColor.r) * alpha) / 255;
    uchar g = ((blendColor.g - sourceColor.g) * alpha) / 255;
    uchar b = ((blendColor.b - sourceColor.b) * alpha) / 255;

    blendColor.r = sourceColor.r + r;
    blendColor.g = sourceColor.g + g;
    blendColor.b = sourceColor.b + b;

    v_out->rgba = blendColor;
}

void gaussBlur_4 (scl, tcl) {
    int[] bxs = boxesForGauss(radius, 3);
    boxBlur_4 (scl, tcl, (bxs[0] - 1) / 2);
    boxBlur_4 (tcl, scl, (bxs[1] - 1) / 2);
    boxBlur_4 (scl, tcl, (bxs[2] - 1) / 2);
}
void boxBlur_4 (scl, tcl, radius) {
    for (int i = 0; i < scl.length; i++) {
        tcl[i] = scl[i];
    }
    boxBlurH_4(tcl, scl, radius);
    boxBlurT_4(scl, tcl, radius);
}
void boxBlurH_4 (scl, tcl, radius) {
    int iarr = 1 / (radius + radius + 1);
    for (uint i = 0; i < h; i++) {
        int ti = i * width;
        int li = ti;
        int ri = ti + radius;
        int fv = scl[ti]
        int lv = scl[ti + width - 1]
        int val = (radius + 1) * fv;

        for (var j = 0; j < r; j++) {
            val += scl[ti + j];
        }
        for (var j = 0; j <= r; j++) {
            val += scl[ri++] - fv;
            tcl[ti++] = round(val * iarr); // Math.round
        }
        for (var j = r + 1; j < width - r; j++) {
            val += scl[ri++] - scl[li++];
            tcl[ti++] = round(val * iarr); // Math.round
        }
        for (var j = width - r; j < width;   j++) {
            val += lv - scl[li++];
            tcl[ti++] = round(val * iarr); // Math.round
        }
    }
}
void boxBlurT_4 (scl, tcl, radius) {
    float iarr = 1 / (radius + radius + 1);
    for (uint i = 0; i < width; i++) {
        int ti = i;
        int li = ti;
        int ri = ti + radius * width;
        int fv = scl[ti];
        int lv = scl[ti + width * (height - 1)]
        int val = (radius + 1) * fv;

        for (int j = 0; j <  r; j++) {
            val += scl[ti + j * width];
        }
        for (int j = 0; j <= r; j++) {
            val += scl[ri] - fv;
            tcl[ti] = Math.round(val * iarr); //Math.round
            ri += width; ti += width;
        }
        for (int j = r + 1; j <  h - r; j++) {
            val += scl[ri] - scl[li];
            tcl[ti] = Math.round(val * iarr); // Math.round
            li += width; ri += width; ti += width;
        }
        for (int j = h - r; j <  h;     j++) {
            val += lv      - scl[li];
            tcl[ti] = Math.round(val * iarr); // Math.round
            li += width; ti += width;
        }
    }
}
