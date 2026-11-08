package com.kyro.app.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "macros")
data class Macro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val triggerPhrase: String,
    val actionsJson: String // Serialized list of actions
)
