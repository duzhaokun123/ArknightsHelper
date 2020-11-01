package com.duzhaokun123.arknightshelper.core.imgreco.ocr

import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.minus
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.resize
import com.duzhaokun123.arknightshelper.core.imgreco.ocr.Util.times
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

object Imgops {
    const val TAG = "Imgops"

    fun uniformSize(img1: Mat, img2: Mat): Pair<Mat, Mat> {
        var re1 = img1
        var re2 = img2

        when {
            re1.height() < re2.height() -> re2 = re2.resize(re1.size())
            re1.height() > re2.height() -> re1 = re1.resize(re2.size())
            re1.width() != re2.width() -> re1 = re1.resize(re2.size())
        }

        return Pair(re1, re2)
    }

    /**
     * def compare_mse(mat1, mat2):
     *     """max 65025 (255**2) for 8bpc image"""
     *     mat1 = np.asarray(mat1)
     *     mat2 = np.asarray(mat2)
     *     assert (mat1.shape == mat2.shape)
     *     diff = mat1.astype(np.float32) - mat2.astype(np.float32)
     *     mse = np.mean(diff * diff)
     *     return mse
     */
    fun compareMse(img1: Mat, img2: Mat): Scalar {
        assert(img1.size() == img2.size() && img1.channels() == img2.channels())
        val diff = img1 - img2
        return Core.mean(diff * diff)
    }

    fun compareCcoeff(img1: Mat, img2: Mat): Double {
        assert(img1.size() == img2.size() && img1.channels() == img2.channels())
        val result = Mat()
        Imgproc.matchTemplate(img1, img2, result, Imgproc.TM_CCOEFF_NORMED)
        return result[0, 0][0]
    }
}


