package com.example.task_6.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.task_6.databinding.FragmentHomeBinding
import kotlin.math.sqrt

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonCalculateTask1.setOnClickListener { Task1() }

        return root
    }

    class ElectricPowerUnit(
        val efficiencyFactor: Double,
        val loadFactor: Double,
        val nominalPower: Double,
        val unitCount: Double,
        val actualPower: Double,
        val variationCoefficient: Double,
        val reactivePowerCoefficient: Double
    ) {
        var rs1rivn: Double = 0.0
    }

    class EPStorage(
        val nomPotEp: Double,
        val koefVik: Double,
        val koefReakPot: Double
    ) {
        var sumKvNPTg: Double = 0.0
        var rozReakNav: Double = 0.0
        var povnPotu: Double = 0.0
        var rozGroupSt: Double = 0.0
        var ForgroupKVik: Double = 0.0
        var ForeffCount: Double = 0.0
        var groupKVik: Double = 0.0
        var effCount: Double = 0.0
        val rozKAkP: Double = 1.25
        var rozAkNav: Double = 0.0
        var sumKvNP: Double = 0.0
        val eps = mutableMapOf(
            "grinding" to ElectricPowerUnit(0.92, 0.9, 0.38, 4.0, 20.0, 0.15, 1.33),
            "drilling" to ElectricPowerUnit(0.92, 0.9, 0.38, 2.0, 14.0, 0.12, 1.0),
            "fuguval" to ElectricPowerUnit(0.92, 0.9, 0.38, 4.0, 42.0, 0.15, 1.33),
            "circular" to ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 36.0, 0.3, 1.52),
            "press" to ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 20.0, 0.5, 0.75),
            "polishing" to ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 40.0, 0.2, 1.0),
            "milling" to ElectricPowerUnit(0.92, 0.9, 0.38, 2.0, 32.0, 0.2, 1.0),
            "fan" to ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 20.0, 0.65, 0.75)
        )

        fun calculateGroup() {
            eps["grinding"] = ElectricPowerUnit(0.92, 0.9, 0.38, 4.0, nomPotEp, 0.15, 1.33)
            eps["polishing"] = ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 40.0, koefVik, 1.0)
            eps["circular"] = ElectricPowerUnit(0.92, 0.9, 0.38, 1.0, 36.0, 0.3, koefReakPot)

            groupKVik = 0.0
            sumKvNP = 0.0
            sumKvNPTg = 0.0
            effCount = 0.0
            ForgroupKVik = 0.0
            ForeffCount = 0.0
            eps.forEach { (_, value) ->
                value.rs1rivn = (value.unitCount * value.actualPower) /
                        (sqrt(3.0) * value.nominalPower * value.loadFactor * value.efficiencyFactor)
                sumKvNP += (value.variationCoefficient * value.actualPower) * value.unitCount
                sumKvNPTg += (value.variationCoefficient * value.actualPower) * value.reactivePowerCoefficient * value.unitCount
                ForgroupKVik += (value.unitCount * value.actualPower)
                ForeffCount += (value.unitCount * value.actualPower * value.actualPower)
            }
            groupKVik = sumKvNP / ForgroupKVik
            effCount = ForgroupKVik * ForgroupKVik / ForeffCount
            val ep = eps["fan"]!!
            rozAkNav = rozKAkP * sumKvNP
            rozReakNav = rozKAkP * sumKvNPTg
            povnPotu = sqrt(rozAkNav * rozAkNav + rozReakNav * rozReakNav)
            rozGroupSt = rozAkNav / ep.nominalPower
        }
    }

    private fun round(num: Double) = "%.3f".format(num)

    private fun Task1()
    {
        val nomPotEp = binding.nomPotEp.text.toString().toDouble()
        val koefVik = binding.koefVik.text.toString().toDouble()
        val koefReakPot = binding.koefReakPot.text.toString().toDouble()

        val ePStorage = EPStorage(nomPotEp, koefVik, koefReakPot)
        ePStorage.calculateGroup()

        val cehPn = 2330
        val cehPn2 = 96399
        val cehnPnKv = 752
        val cehRKAP = 0.7
        val cehKvPntg = 657
        val cehGroupKVik = cehnPnKv/cehPn
        val cehNe = cehPn*cehPn / cehPn2
        val cehRAN = cehRKAP * cehnPnKv
        val cehRRN = cehRKAP * cehKvPntg
        val cehPP = sqrt(cehRAN*cehRAN + cehRRN*cehRRN)
        val cehRGS = cehRAN / 0.38;
        
        var output = 
            "Груповий коефіцієнт використання для ШР1=ШР2=ШР3: ${round(ePStorage.groupKVik)}\n"+
            "Ефективна кількість ЕП для ШР1=ШР2=ШР3: ${round(ePStorage.effCount)}\n"+
            "Розрахунковий коефіцієнт активної потужності для ШР1=ШР2=ШР3: ${round(ePStorage.rozKAkP)}\n"+
            "Розрахункове активне навантаження для ШР1=ШР2=ШР3: ${round(ePStorage.rozAkNav)}\n"+
            "Розрахункове реактивне навантаження для ШР1=ШР2=ШР3: ${round(ePStorage.rozReakNav)}\n"+
            "Повна потужність для ШР1=ШР2=ШР3: ${round(ePStorage.povnPotu)}\n"+
            "Розрахунковий груповий струм для ШР1=ШР2=ШР3: ${round(ePStorage.rozGroupSt)}\n"+
            "Коефіцієнти використання цеху в цілому: ${cehGroupKVik}\n"+
            "Ефективна кількість ЕП цеху в цілому: ${cehNe}\n"+
            "Розрахунковий коефіцієнт активної потужності цеху в цілому: ${round(cehRKAP)}\n"+
            "Розрахункове активне навантаження на шинах 0,38 кВ ТП: ${round(cehRAN)}\n"+
            "Розрахункове реактивне навантаження на шинах 0,38 кВ ТП: ${round(cehRRN)}\n"+
            "Повна потужність на шинах 0,38 кВ ТП: ${round(cehPP)}\n"+
            "Розрахунковий груповий струм на шинах 0,38 кВ ТП: ${round(cehRGS)}"

        binding.outputTask1.text = output;
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}