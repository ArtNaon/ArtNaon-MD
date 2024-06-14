package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(

	@field:SerializedName("result")
	val result: DetailResult? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DetailResult(

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("genre")
	val genre: String? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("picture")
	val picture: String? = null
)
