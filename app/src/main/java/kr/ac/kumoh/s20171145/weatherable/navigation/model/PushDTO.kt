package kr.ac.kumoh.s20171145.weatherable.navigation.model

data class PushDTO(
    var to : String? = null,
    var notification : Notification = Notification()
){
    data class Notification(
        var body : String? = null,
        var title : String? = null
    )
}