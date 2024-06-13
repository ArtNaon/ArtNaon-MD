package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(

	@field:SerializedName("data")
	val data: Data,

	@field:SerializedName("message")
	val message: String,

	@field:SerializedName("status")
	val status: String
)

data class Data(

	@field:SerializedName("genre")
	val genre: String,

	@field:SerializedName("description")
	val description: String,

	@field:SerializedName("Url")
	val url: String
)
