package com.example.personalworkoutnotebook.model

import androidx.room.*
import com.example.personalworkoutnotebook.extension.toFirstUpperCase
import java.io.Serializable

data class BioParameter(
    val id: Long,
    val name: String,
    val values: List<BioParameterValue>

): Serializable {}

@Entity(tableName = "bio_parameters")
data class RoomBioParameter(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "name") val name: String

)

data class RoomBioParameterWithValues(
    @Embedded val roomBioParameter: RoomBioParameter,
    @Relation(parentColumn = "id", entityColumn = "bioParameterId", entity = RoomBioParameterValue::class)
    val values: List<RoomBioParameterValue>
)

fun BioParameter.toRoom() = RoomBioParameter(
    id = this.id,
    name = this.name.toFirstUpperCase()

)

fun RoomBioParameterWithValues.toModel() = BioParameter(
    id = this.roomBioParameter.id,
    name = this.roomBioParameter.name,
    values = this.values.map { it.toModel() }

)