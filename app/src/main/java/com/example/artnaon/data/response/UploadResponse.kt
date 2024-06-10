package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(
	@SerializedName("data")
	val data: Data? = null,

	@SerializedName("message")
	val message: String? = null,

	@SerializedName("status")
	val status: String? = null
)

data class Data(
	@SerializedName("genre")
	val genre: String? = null,

	@SerializedName("description")
	val description: String? = null,

	@SerializedName("Url")
	val url: String? = null
)