package com.dalua.app.utils

import android.content.res.Resources
import com.dalua.app.R
import com.dalua.app.models.ResponseOtaFiles

class ResourceOTARepository(private val mResources: Resources) {

    fun getBlazeXFile(): ByteArray {
        val inputStream = mResources.openRawResource(R.raw.blazex_210011)
        return inputStream.readBytes()
    }

    fun getX4File(): ByteArray {
        val inputStream = mResources.openRawResource(R.raw.x4_210011)
        return inputStream.readBytes()
    }

    fun getBlazeXOtaFile(): ResponseOtaFiles.OtaFile {
        val otaFile = ResponseOtaFiles.OtaFile()
        otaFile.bytesArray = mResources.openRawResource(R.raw.blazex_210011).readBytes()
        otaFile.name = "BlazeX.210011.bin"
        otaFile.selected = false
        otaFile.product.apply {
            name = "BlazeX"
            id = 1
            imageId = R.drawable.blazex_image
        }
        return otaFile
    }

    fun getX4OtaFile(): ResponseOtaFiles.OtaFile {
        val otaFile = ResponseOtaFiles.OtaFile()
        otaFile.bytesArray = mResources.openRawResource(R.raw.x4_210011).readBytes()
        otaFile.name = "X4.210011.bin"
        otaFile.selected = false
        otaFile.product.apply {
            name = "X4"
            id = 2
            imageId = R.drawable.x4_image

        }
        return otaFile
    }
}