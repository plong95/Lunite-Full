package com.ruse.world.content;

import com.ruse.model.container.impl.Equipment;
import com.ruse.model.definitions.NPCDrops;
import com.ruse.world.content.skill.impl.summoning.Familiar;
import com.ruse.world.entity.impl.player.Player;

public class CustomDropUtils {


    public static int[][] DRITEMS = {

            {23856, 50},//

            {23836, 30},//
            {23837, 15},//

            {23831, 25},//

            {23827, 40},//
            {23883, 40},//
            {23828, 80},//
            {23838, 80},//
            {23884, 80},//


            {23743, 35},//
            {23744, 35},//
            {23745, 35},//


            {23860, 35},//
            {23861, 35},//
            {23862, 35},//
            {23870, 35},//
            {23871, 35},//
            {23872, 35},//
            {23876, 35},//
            {23877, 35},//
            {23878, 35},//


            {23821, 15},//
            {23822, 15},//
            {23823, 15},//
            {23824, 15},//
            {23825, 15},//
            {23817, 25},//
            {23818, 25},//
            {23819, 25},//
            {23820, 15},//


            {23801, 15},//
            {23807, 20},//
            {23800, 25},//
            {23809, 10},//
            {23811, 20},//

            {23785, 20},//
            {23786, 20},//
            {23787, 20},//

            {23788, 10},//

            {23789, 10},//
            {23790, 10},//
            {23791, 10},//
            {23792, 10},//
            {23793, 10},//


            {23758, 50},//


            {23737, 75},//
            {23738, 60},//
            {23739, 75},//
            {23740, 75},//
            {23741, 75},//
            {23742, 40},//



            {23013, 25},//

            {23014, 30},//
            {23016, 30},//
            {23017, 30},//
            {23018, 30},//

            {1037, 35},//
            {23814, 35},//




            {23712, 45},//
            {23713, 60},//
            {23714, 60},//
            {23715, 60},//


            {23688, 40},//
            {23705, 30},//


            {23704, 45},//
            {23854, 45},//
//            {23834, 45},//

            {23689, 80},//

            {23699, 45},//
            {23701, 45},//
            {23703, 45},//

            {23696, 30},//
            {23888, 40},//

            {13800, 15},//
            {13801, 25},//

            {23660, 25},//

            {23651, 60},//
            {23653, 60},//

            {23638, 90},//
            {23639, 90},//
            {23640, 90},//
            {23641, 90},//
            {23642, 90},//
            {23643, 90},//


            {23840, 120},//
            {23841, 120},//
            {23842, 120},//


            {23604, 45},//
            {23775, 45},//
            {23616, 10},//
            {23618, 30},//
            {23774, 30},//
            {23772, 20},//

            {23589, 25},//
            {23590, 25},//
            {23592, 30},//
            {23593, 30},//


            {12608, 5},//


            {4446, 10},//
            {19886, 10},//

            //season
            {14050, 10},//
            {14051, 10},//
			{14052, 10},//
			{14053, 10},//
			{14055, 10},//

            //hween
            {10723, 20},//
            {19929, 20},//
            {19928, 20},//

            {11789, 15},//
            {11790, 25},//
            {23100, 15},//
            {23101, 20},//

            {23542, 20},//
            {23543, 20},//
            {23544, 30},//
            {23621, 30},//

            {23558, 40},//
            {23559, 40},//
            {23560, 40},//

            {22100, 20},//
            {22101, 20},//
            {22102, 20},//
            {22103, 30},//
            {22104, 30},//
            {22105, 50},//
            {22109, 60},//
            {23717, 60},//
            {23595, 60},//
            {23620, 60},//
            {23535, 80},//
            {22111, 35},//
            {23594, 35},//
            {23513, 45},//
            {22113, 35},//
            {22114, 35},//
            {22115, 35},//
            {23619, 35},//

            {23455, 35},//

            /* {23013, 25},//
             {23014, 35},//
             {23015, 35},//
             {23016, 35},//
             {23017, 35},//*/

            {22000, 4},//
            {22001, 8},//
            {22002, 12},//
            {22003, 16},//
            {22004, 20},//
            {22005, 40},//
            {23354, 40},//

            {23541, 40},//
            {23540, 40},//

            {20086, 10},//
            {20087, 10},//
            {9939, 15},//
            {7995, 50},//
            {12630, 25},//
            {15449, 10},//

            {15450, 10},//
            {4373, 10},//
            {20088, 10},//
            {18419, 7},//
            {18416, 7},//
            {18418, 7},//
            {18417, 7},//

            {11320, 5},//
            {4684, 15},//
            {4685, 15},//
            {4686, 15},//
            {15115, 10},//
            {15116, 10},//
            {15117, 10},//
            {15118, 10},//
            {15119, 10},//
            {21607, 10},//
            {21608, 10},//
            {21609, 10},//
            {21611, 10},//
            {21612, 10},//


            {8828, 10},//
            {15645, 10},//
            {15642, 10},//
            {11321, 5},//
            {8829, 10},//
            {15643, 10},//
            {15646, 10},//
            {11322, 5},//
            {8833, 10},//
            {15644, 10},//
            {15647, 10},//
            {11340, 5},//
            {11341, 5},//
            {11342, 5},//
            {11421, 5},//


            {11422, 5},//
            {11423, 5},//
            {20089, 10},//
            {8274, 10},//
            {8273, 10},//
            {20099, 10},//
            {20092, 20},//
            {20093, 20},//
            {19888, 20},//
            {18823, 20},//
            {18888, 30},//

            //{13555, 40},//
            {23000, 45},//
            {23710, 30},//

            {18818, 30},//
            {20091, 10},//
            {20098, 10},//
            {8253, 15},//
            {20090, 10},//
            {19810, 20},//
            {2572, 5},//
            {8335, 20},//
           // {1485, 205},//
            {3324, 10},//
            {15401, 15},//


            {14910, 5},//
            {14911, 5},//
            {14912, 5},//
            {14916, 5},//
            {14917, 5},//
            {14918, 5},//
            {14921, 5},//
            {14922, 5},//
            {14923, 5},//


            {4017, 10},//
            {8001, 30},//
            {4018, 10},//
            {21610, 10},//
            {8410, 20},//
            {3260, 25},//
            {8411, 20},//
            {8412, 20},//
            {8087, 10},//
            {8088, 10},//
            {8089, 10},//


            {12535, 25},//
            {12537, 30},//
            {17011, 25},//
            {17013, 30},//
            {13556, 30},//
            {23537, 30},//
            {5012, 25},//
            {5011, 30},//
            {5010, 30},//

            {20591, 25},//
            {20400, 40},//

            {23538, 25},//
            {23539, 25},//

            {18889, 25},//
            {22117, 35},//
            {18881, 20},//
            {18883, 20},//
            {18887, 25},//
            {18885, 25},//
            {9054, 15},//
            {9055, 15},//
            {9056, 15},//
            {9057, 15},//
            {9058, 15},//


            //auras
            {23044, 3},//
            {23045, 6},//
            {23046, 10},//
            {23047, 13},//
            {23048, 16},//
            {23049, 20},//

            {23664, 40},//


            {23373, 40},//
            {23596, 40},//

            {23597, 40},//
            {23220, 40},//
            {23221, 40},//
            {23222, 40},//
            {23891, 40},//
            {23716, 40},//

            {23566, 40},//
            {23567, 40},//
            {23568, 40},//

            {22089, 40},//
            {23832, 120},//
            {23839, 90},//
            {23474, 40},//


            {23310, 20},//
            {23311, 20},//
            {23312, 20},//

            {23313, 35},//

            {23351, 50},//
            {23352, 50},//
            {23353, 50},//


            {23377, 25},//



            {23391, 10},//
            {23392, 10},//
            {23393, 25},//

            {23458, 35},//

            {23403, 40},//
            {23512, 40},//



            {23394, 30},//
            {23395, 30},//
            {23396, 30},//
            {23397, 40},//
            {23398, 40},//
            {23399, 40},//
            {23718, 40},//

            {23465, 25},//
            {23466, 25},//
            {23467, 25},//
            {23468, 25},//
            {23469, 25},//
            {23470, 25},//
            {23471, 25},//
            {23472, 25},//
            {23473, 25},//


            {23501, 20},//
            {23502, 20},//
            {23503, 20},//
            {23504, 30},//
            {23505, 30},//

            {23497, 15},//
            {23498, 15},//
            {23499, 15},//

            {23482, 10},//
            {23483, 10},//
            {23484, 10},//

            {23485, 15},//
            {23486, 15},//
            {23487, 15},//

            {23488, 20},//
            {23489, 20},//
            {23490, 20},//


            {23725, 40},//

            {23523, 35},//
            {23524, 35},//
            {23889, 35},//

            {23525, 30},//
            {23526, 30},//
            {23527, 30},//
            {23528, 30},//
            {23529, 30},//
            {23530, 30},//
            {23531, 30},//
            {23532, 30},//
            {23533, 30},//



            {23561, 30},//
            {23562, 30},//
            {23563, 30},//
            {23564, 40},//
            {23565, 40},//


            {23720, 30},//
            {23721, 30},//
            {23722, 30},//
            {23723, 40},//
            {23724, 40},//

    };

    public static final int[][] DRPETS = {

            {11873, 65},//
            {10031, 65},//

            {28025, 120},//
            {28492, 100},//


            {10024, 65},//
            {10025, 75},//



            {9021, 70},//
            {10013, 80},//


            {4414, 1},//
            {3001, 3},//
            {1892, 5},//
            {1894, 7},//
            {5001, 10},//
            {3377, 15},//
            {1906, 25},//
            {1801, 50},//
            {3309, 10},//
            {302, 10},//
            {1890, 20},//
            {1893, 30},//
            {1904, 40},//
            {1902, 45},//
            {1910, 60},//
            {9013, 60},//
            {9016, 60},//

            {9032, 50},//
            {9033, 60},//\


            {9819, 50},//
            {9820, 50},//
            {9821, 50},//
            {9822, 50},//

            {9833, 70},//
            {9830, 50},//

            {9861, 60},//
            {9862, 70},//
            {9863, 70},//
            {9864, 70},//
            {9865, 70},//
            {9866, 70},//
            {9867, 70},//
            {9868, 70},//

            {9872, 60},//
            {9873, 60},//
            {9874, 60},//

            {9875, 70},//
            {9876, 75},//

            {10002, 100},//
            {10003, 100},//
            {10004, 50},//


            {10011, 60},//
            {10012, 65},//
            {10121, 65},//
    };


    public static int getDropRate(int id) {
        if (id == 22089) {
            return 90;
        }
        if (id == 23663) {
            return 90;
        }

        if (id >= 23015 && id <= 23017 )
            return 30;
        if (id == 8001) {
            return 40;
        }
        if (id == 23719) {
            return 40;
        }
        if (id == 23513) {
            return 60;
        }
        if (id == 23535) {
            return 80;
        }


        if (id >= 23014 && id <= 23018){
            return 30;
        }

        for (int[] item : DRITEMS) {
            if (item[0] == id) {
                return item[1];
            }
        }
        if (id == 23621) {
            return 35;
        }
        if (id == 21613) {
            return 50;
        }

        return 0;
    }


    public static int getDoubleDropChance(Player player) {

        int dropChance = NPCDrops.dropRateBoost(player) / 20;

        if (player.getDoubleDDRTimer() > 0) {
            dropChance += 100;
        }

        return dropChance;
    }


}
