package com.example.personalworkoutnotebook.model

import androidx.room.*
import java.util.*

data class BioParameterValue(
    val id: Long,
    val bioParameterId : Long,
    val date: Calendar,
    val value: Double
)

@Entity(tableName = "bio_value", foreignKeys = [
    ForeignKey(entity = RoomBioParameter::class,
    parentColumns = ["id"],
    childColumns = ["bioParameterId"],
    onDelete = ForeignKey.CASCADE)])
@TypeConverters(DateConverter::class)
data class RoomBioParameterValue(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id:Long,
    @ColumnInfo(name = "bioParameterId", index = true) val bioParameterId:Long,
    @ColumnInfo(name = "date") val date: Calendar,
    @ColumnInfo(name = "value") val value: Double
)

fun BioParameterValue.toRoom() = RoomBioParameterValue(
    id = this.id,
    bioParameterId = this.bioParameterId,
    date = this.date,
    value = this.value
)

fun RoomBioParameterValue.toModel() = BioParameterValue(
    id = this.id,
    bioParameterId = this.bioParameterId,
    date = this.date,
    value = this.value
)