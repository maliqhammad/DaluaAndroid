package com.dalua.app.utils

class ColorOpacities {

    companion object {
        fun getColorString(pc: Int, colorString: String): String {

            return when (pc) {
                100 -> {
                    "#FF$colorString"
                }

                99 -> {
                    "#FC$colorString"
                }
                98 -> {
                    "#FA$colorString"
                }
                97 -> {
                    "#F7$colorString"
                }
                96 -> {
                    "#F5$colorString"
                }
                95 -> {
                    "#F2$colorString"
                }
                94 -> {
                    "#F0$colorString"
                }
                93 -> {
                    "#ED$colorString"
                }
                92 -> {
                    "#EB$colorString"
                }
                91 -> {
                    "#E8$colorString"
                }
                90 -> {
                    "#E6$colorString"
                }
                89 -> {
                    "#E3$colorString"
                }
                88 -> {
                    "#E0$colorString"
                }
                87 -> {
                    "#DE$colorString"
                }
                86 -> {
                    "#DB$colorString"
                }
                85 -> {
                    "#D9$colorString"
                }
                84 -> {
                    "#D6$colorString"
                }
                83 -> {
                    "#D4$colorString"
                }
                82 -> {
                    "#D1$colorString"
                }
                81 -> {
                    "#CF$colorString"
                }
                80 -> {
                    "#CC$colorString"
                }
                79 -> {
                    "#C9$colorString"
                }
                78 -> {
                    "#C7$colorString"
                }
                77 -> {
                    "#C4$colorString"
                }
                76 -> {
                    "#C2$colorString"
                }
                75 -> {
                    "#BF$colorString"
                }
                74 -> {
                    "#BD$colorString"
                }
                73 -> {
                    "#BA$colorString"
                }
                72 -> {
                    "#B8$colorString"
                }
                71 -> {
                    "#B5$colorString"
                }
                70 -> {
                    "#B3$colorString"
                }
                69 -> {
                    "#B0$colorString"
                }
                68 -> {
                    "#AD$colorString"
                }
                67 -> {
                    "#AB$colorString"
                }
                66 -> {
                    "#A8$colorString"
                }
                65 -> {
                    "#A6$colorString"
                }
                64 -> {
                    "#A3$colorString"
                }
                63 -> {
                    "#A1$colorString"
                }
                62 -> {
                    "#9E$colorString"
                }
                61 -> {
                    "#9C$colorString"
                }
                60 -> {
                    "#99$colorString"
                }
                59 -> {
                    "#96$colorString"
                }
                58 -> {
                    "#94$colorString"
                }
                57 -> {
                    "#91$colorString"
                }
                56 -> {
                    "#8F$colorString"
                }
                55 -> {
                    "#8C$colorString"
                }
                54 -> {
                    "#8A$colorString"
                }
                53 -> {
                    "#87$colorString"
                }
                52 -> {
                    "#85$colorString"
                }
                51 -> {
                    "#82$colorString"
                }
                50 -> {
                    "#80$colorString"
                }
                49 -> {
                    "#7D$colorString"
                }
                48 -> {
                    "#7A$colorString"
                }
                47 -> {
                    "#78$colorString"
                }
                46 -> {
                    "#75$colorString"
                }
                45 -> {
                    "#73$colorString"
                }
                44 -> {
                    "#70$colorString"
                }
                43 -> {
                    "#6E$colorString"
                }
                42 -> {
                    "#6B$colorString"
                }
                41 -> {
                    "#69$colorString"
                }
                40 -> {
                    "#66$colorString"
                }
                39 -> {
                    "#63$colorString"
                }
                38 -> {
                    "#61$colorString"
                }
                37 -> {
                    "#5E$colorString"
                }
                36 -> {
                    "#5C$colorString"
                }
                35 -> {
                    "#59$colorString"
                }
                34 -> {
                    "#57$colorString"
                }
                33 -> {
                    "#54$colorString"
                }
                32 -> {
                    "#52$colorString"
                }
                31 -> {
                    "#4F$colorString"
                }
                30 -> {
                    "#4D$colorString"
                }
                29 -> {
                    "#4A$colorString"
                }
                28 -> {
                    "#47$colorString"
                }
                27 -> {
                    "#45$colorString"
                }
                26 -> {
                    "#42$colorString"
                }
                25 -> {
                    "#40$colorString"
                }
                24 -> {
                    "#3D$colorString"
                }
                23 -> {
                    "#3B$colorString"
                }
                22 -> {
                    "#38$colorString"
                }
                21 -> {
                    "#36$colorString"
                }
                20 -> {
                    "#33$colorString"
                }
                19 -> {
                    "#30$colorString"
                }
                18 -> {
                    "#2E$colorString"
                }
                17 -> {
                    "#2B$colorString"
                }
                16 -> {
                    "#29$colorString"
                }
                15 -> {
                    "#26$colorString"
                }
                14 -> {
                    "#24$colorString"
                }
                13 -> {
                    "#21$colorString"
                }
                12 -> {
                    "#1F$colorString"
                }
                11 -> {
                    "#1C$colorString"
                }
                10 -> {
                    "#1A$colorString"
                }
                9 -> {
                    "#17$colorString"
                }
                8 -> {
                    "#14$colorString"
                }
                7 -> {
                    "#12$colorString"
                }
                6 -> {
                    "#0F$colorString"
                }
                5 -> {
                    "#0D$colorString"
                }
                4 -> {
                    "#0A$colorString"
                }
                3 -> {
                    "#08$colorString"
                }
                2 -> {
                    "#05$colorString"
                }
                1 -> {
                    "#03$colorString"
                }
                0 -> {
                    "#00$colorString"
                }

                else -> {
                    "#00$colorString"
                }
            }
        }

        fun revertOpacity(pc: Int): Int {

            return when (pc) {
                100 -> {
0
                }

                99 -> {
                    1
                }
                98 -> {
                    2
                }
                97 -> {
                    3
                }
                96 -> {
                    4
                }
                95 -> {
                    5
                }
                94 -> {
                    6
                }
                93 -> {
                    7
                }
                92 -> {
                    8
                }
                91 -> {
                    9
                }
                90 -> {
                    10
                }
                89 -> {
                    11
                }
                88 -> {
                    12
                }
                87 -> {
                    13
                }
                86 -> {
                    14
                }
                85 -> {
                    15
                }
                84 -> {
                    16
                }
                83 -> {
                    17
                }
                82 -> {
                    18
                }
                81 -> {
                    19
                }
                80 -> {
                    20
                }
                79 -> {
                    21
                }
                78 -> {
                    22
                }
                77 -> {
                    23
                }
                76 -> {
                    24
                }
                75 -> {
                    25
                }
                74 -> {
                    26
                }
                73 -> {
                    27
                }
                72 -> {
                    28
                }
                71 -> {
                    29
                }
                70 -> {
                    30
                }
                69 -> {
                    31
                }
                68 -> {
                    32
                }
                67 -> {
                    33
                }
                66 -> {
                    34
                }
                65 -> {
                    35
                }
                64 -> {
                    36
                }
                63 -> {
                    37
                }
                62 -> {
                    38
                }
                61 -> {
                    39
                }
                60 -> {
                    40
                }
                59 -> {
                    41
                }
                58 -> {
                    42
                }
                57 -> {
                    43
                }
                56 -> {
                    44
                }
                55 -> {
                    45
                }
                54 -> {
                    46
                }
                53 -> {
                    47
                }
                52 -> {
                    48
                }
                51 -> {
                    49
                }
                50 -> {
                    50
                }
                49 -> {
                    51
                }
                48 -> {
                    52
                }
                47 -> {
                    53
                }
                46 -> {
                    54
                }
                45 -> {
                    55
                }
                44 -> {
                    56
                }
                43 -> {
                    57
                }
                42 -> {
                    58
                }
                41 -> {
                    59
                }
                40 -> {
                    60
                }
                39 -> {
                    61
                }
                38 -> {
                    62
                }
                37 -> {
                    63
                }
                36 -> {
                    64
                }
                35 -> {
                    65
                }
                34 -> {
                    66
                }
                33 -> {
                    67
                }
                32 -> {
                    68
                }
                31 -> {
                    69
                }
                30 -> {
                    70
                }
                29 -> {
                    71
                }
                28 -> {
                    72
                }
                27 -> {
                    73
                }
                26 -> {
                    74
                }
                25 -> {
                    75
                }
                24 -> {
                    76
                }
                23 -> {
                    77
                }
                22 -> {
                    78
                }
                21 -> {
                    79
                }
                20 -> {
                    80
                }
                19 -> {
                    81
                }
                18 -> {
                    82
                }
                17 -> {
                    83
                }
                16 -> {
                    84
                }
                15 -> {
                    85
                }
                14 -> {
                    86
                }
                13 -> {
                    87
                }
                12 -> {
                    88
                }
                11 -> {
                    89
                }
                10 -> {
                    90
                }
                9 -> {
                    91
                }
                8 -> {
                    92
                }
                7 -> {
                    93
                }
                6 -> {
                    94
                }
                5 -> {
                    95
                }
                4 -> {
                    96
                }
                3 -> {
                    97
                }
                2 -> {
                    98
                }
                1 -> {
                    99
                }
                0 -> {
                    100
                }

                else -> {
                    100
                }
            }
        }

        fun getColorString(pc: Int, colorString: Int): String {

            return when (pc) {
                100 -> {
                    "#FF$colorString"
                }

                99 -> {
                    "#FC$colorString"
                }
                98 -> {
                    "#FA$colorString"
                }
                97 -> {
                    "#F7$colorString"
                }
                96 -> {
                    "#F5$colorString"
                }
                95 -> {
                    "#F2$colorString"
                }
                94 -> {
                    "#F0$colorString"
                }
                93 -> {
                    "#ED$colorString"
                }
                92 -> {
                    "#EB$colorString"
                }
                91 -> {
                    "#E8$colorString"
                }
                90 -> {
                    "#E6$colorString"
                }
                89 -> {
                    "#E3$colorString"
                }
                88 -> {
                    "#E0$colorString"
                }
                87 -> {
                    "#DE$colorString"
                }
                86 -> {
                    "#DB$colorString"
                }
                85 -> {
                    "#D9$colorString"
                }
                84 -> {
                    "#D6$colorString"
                }
                83 -> {
                    "#D4$colorString"
                }
                82 -> {
                    "#D1$colorString"
                }
                81 -> {
                    "#CF$colorString"
                }
                80 -> {
                    "#CC$colorString"
                }
                79 -> {
                    "#C9$colorString"
                }
                78 -> {
                    "#C7$colorString"
                }
                77 -> {
                    "#C4$colorString"
                }
                76 -> {
                    "#C2$colorString"
                }
                75 -> {
                    "#BF$colorString"
                }
                74 -> {
                    "#BD$colorString"
                }
                73 -> {
                    "#BA$colorString"
                }
                72 -> {
                    "#B8$colorString"
                }
                71 -> {
                    "#B5$colorString"
                }
                70 -> {
                    "#B3$colorString"
                }
                69 -> {
                    "#B0$colorString"
                }
                68 -> {
                    "#AD$colorString"
                }
                67 -> {
                    "#AB$colorString"
                }
                66 -> {
                    "#A8$colorString"
                }
                65 -> {
                    "#A6$colorString"
                }
                64 -> {
                    "#A3$colorString"
                }
                63 -> {
                    "#A1$colorString"
                }
                62 -> {
                    "#9E$colorString"
                }
                61 -> {
                    "#9C$colorString"
                }
                60 -> {
                    "#99$colorString"
                }
                59 -> {
                    "#96$colorString"
                }
                58 -> {
                    "#94$colorString"
                }
                57 -> {
                    "#91$colorString"
                }
                56 -> {
                    "#8F$colorString"
                }
                55 -> {
                    "#8C$colorString"
                }
                54 -> {
                    "#8A$colorString"
                }
                53 -> {
                    "#87$colorString"
                }
                52 -> {
                    "#85$colorString"
                }
                51 -> {
                    "#82$colorString"
                }
                50 -> {
                    "#80$colorString"
                }
                49 -> {
                    "#7D$colorString"
                }
                48 -> {
                    "#7A$colorString"
                }
                47 -> {
                    "#78$colorString"
                }
                46 -> {
                    "#75$colorString"
                }
                45 -> {
                    "#73$colorString"
                }
                44 -> {
                    "#70$colorString"
                }
                43 -> {
                    "#6E$colorString"
                }
                42 -> {
                    "#6B$colorString"
                }
                41 -> {
                    "#69$colorString"
                }
                40 -> {
                    "#66$colorString"
                }
                39 -> {
                    "#63$colorString"
                }
                38 -> {
                    "#61$colorString"
                }
                37 -> {
                    "#5E$colorString"
                }
                36 -> {
                    "#5C$colorString"
                }
                35 -> {
                    "#59$colorString"
                }
                34 -> {
                    "#57$colorString"
                }
                33 -> {
                    "#54$colorString"
                }
                32 -> {
                    "#52$colorString"
                }
                31 -> {
                    "#4F$colorString"
                }
                30 -> {
                    "#4D$colorString"
                }
                29 -> {
                    "#4A$colorString"
                }
                28 -> {
                    "#47$colorString"
                }
                27 -> {
                    "#45$colorString"
                }
                26 -> {
                    "#42$colorString"
                }
                25 -> {
                    "#40$colorString"
                }
                24 -> {
                    "#3D$colorString"
                }
                23 -> {
                    "#3B$colorString"
                }
                22 -> {
                    "#38$colorString"
                }
                21 -> {
                    "#36$colorString"
                }
                20 -> {
                    "#33$colorString"
                }
                19 -> {
                    "#30$colorString"
                }
                18 -> {
                    "#2E$colorString"
                }
                17 -> {
                    "#2B$colorString"
                }
                16 -> {
                    "#29$colorString"
                }
                15 -> {
                    "#26$colorString"
                }
                14 -> {
                    "#24$colorString"
                }
                13 -> {
                    "#21$colorString"
                }
                12 -> {
                    "#1F$colorString"
                }
                11 -> {
                    "#1C$colorString"
                }
                10 -> {
                    "#1A$colorString"
                }
                9 -> {
                    "#17$colorString"
                }
                8 -> {
                    "#14$colorString"
                }
                7 -> {
                    "#12$colorString"
                }
                6 -> {
                    "#0F$colorString"
                }
                5 -> {
                    "#0D$colorString"
                }
                4 -> {
                    "#0A$colorString"
                }
                3 -> {
                    "#08$colorString"
                }
                2 -> {
                    "#05$colorString"
                }
                1 -> {
                    "#03$colorString"
                }
                0 -> {
                    "#00$colorString"
                }

                else -> {
                    "#00$colorString"
                }
            }
        }
    }
}