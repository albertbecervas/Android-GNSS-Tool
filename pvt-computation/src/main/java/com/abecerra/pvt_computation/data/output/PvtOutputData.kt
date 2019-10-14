package com.abecerra.pvt_computation.data.output

import com.abecerra.pvt_computation.data.input.ComputationSettings
import com.abecerra.pvt_computation.data.LlaLocation
import com.abecerra.pvt_computation.data.PvtFix
import java.io.Serializable

data class PvtOutputData(
    var pvtFix: PvtFix,
    var refPosition: LlaLocation,
    var computationSettings: ComputationSettings,
    var corrections: Corrections,
    var dop: Dop,
    var residue: Double = 0.0,
    var nSats: Float = 0f
) : Serializable

data class Corrections(
    var gpsIono: Double = 0.0,
    var gpsTropo: Double = 0.0,
    var galIono: Double = 0.0,
    var galTropo: Double = 0.0,
    var freq2: Double = 0.0
)

data class Dop(
    var gDop: Double = -1.0,
    var pDop: Double = -1.0,
    var tDop: Double = -1.0
)


