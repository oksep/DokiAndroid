package com.dokiwa.dokidoki.center.uploader

import android.net.Uri
import com.dokiwa.dokidoki.center.api.Api
import com.google.gson.annotations.SerializedName
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

/**
 * Created by Septenary on 2019/3/2.
 */
object SimpleUploader {

    interface UploadApi {
        @Multipart
        @POST("/api/file/v1/image")
        fun uploadFile(
            @Part file: MultipartBody.Part,
            @Part type: MultipartBody.Part
        ): Single<UploadImageResult>
    }

    enum class ImageType(val type: String) {
        AVATAR("avatar"),
        PICTURE("picture"),
        PRIVATE("private")
    }

    data class UploadImageResult(val image: Image) {
        data class Image(
            @SerializedName("middle_url")
            val middleUrl: String?,
            @SerializedName("raw_url")
            val rawUrl: String?,
            val url: String?
        )

        companion object {
            fun obtainEmptyImageResult(): UploadImageResult {
                return UploadImageResult(Image(null, null, null))
            }
        }
    }


    fun uploadImage(filePath: String, imageType: ImageType): Single<UploadImageResult> {
        return this.uploadImage(File(filePath), imageType)
    }

    fun uploadImage(uri: Uri, imageType: ImageType): Single<UploadImageResult> {
        return this.uploadImage(File(uri.path), imageType)
    }

    fun uploadImage(file: File, imageType: ImageType): Single<UploadImageResult> {

        val requestFile = RequestBody.create(MediaType.parse("image/png"), file)
        val body1 = MultipartBody.Part.createFormData("file", file.name, requestFile)

        val body2 = MultipartBody.Part.createFormData("type", imageType.type)

        return Api.get(UploadApi::class.java).uploadFile(body1, body2)
    }

}