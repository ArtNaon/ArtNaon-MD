package com.example.artnaon.data.response

import com.google.gson.annotations.SerializedName

data class HomeGenreResponse(

	@field:SerializedName("data")
	val data: List<String?>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)
