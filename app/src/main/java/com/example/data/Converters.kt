package com.example.data

import androidx.room.TypeConverter
import com.example.model.StatusSetor
import com.example.model.StatusTrecho
import com.example.model.TipoOcorrencia
import com.example.model.TipoPausa

class Converters {
    @TypeConverter
    fun fromStatusSetor(value: StatusSetor?): String = value?.name ?: StatusSetor.PRONTO.name

    @TypeConverter
    fun toStatusSetor(value: String?): StatusSetor =
        try {
            value?.let { StatusSetor.valueOf(it) } ?: StatusSetor.PRONTO
        } catch (e: Exception) {
            StatusSetor.PRONTO
        }

    @TypeConverter
    fun fromStatusTrecho(value: StatusTrecho?): String = value?.name ?: StatusTrecho.PENDENTE.name

    @TypeConverter
    fun toStatusTrecho(value: String?): StatusTrecho =
        try {
            value?.let { StatusTrecho.valueOf(it) } ?: StatusTrecho.PENDENTE
        } catch (e: Exception) {
            StatusTrecho.PENDENTE
        }

    @TypeConverter
    fun fromTipoOcorrencia(value: TipoOcorrencia?): String = value?.name ?: TipoOcorrencia.OUTRO.name

    @TypeConverter
    fun toTipoOcorrencia(value: String?): TipoOcorrencia =
        try {
            value?.let { TipoOcorrencia.valueOf(it) } ?: TipoOcorrencia.OUTRO
        } catch (e: Exception) {
            TipoOcorrencia.OUTRO
        }

    @TypeConverter
    fun fromTipoPausa(value: TipoPausa?): String = value?.name ?: TipoPausa.DESCANSO.name

    @TypeConverter
    fun toTipoPausa(value: String?): TipoPausa =
        try {
            value?.let { TipoPausa.valueOf(it) } ?: TipoPausa.DESCANSO
        } catch (e: Exception) {
            TipoPausa.DESCANSO
        }
}
