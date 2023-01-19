package com.ruse.world.content.upgrading;

import com.ruse.model.Item;
import lombok.Getter;

import java.util.ArrayList;

import static com.ruse.world.content.upgrading.Upgradeables.UpgradeType.*;

@Getter
public enum Upgradeables {

    HAT(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT1(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT2(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT3(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT4(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT5(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT6(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT7(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT8(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT9(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),
    HAT10(TIER1, new Item(1038, 1), new Item(1050, 1), 104000, 50),

    WHIP(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP1(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP2(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP3(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP4(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP5(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP6(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP7(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP8(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),
    WHIP9(TIER2, new Item(4151, 1), new Item(1050, 1), 1456000, 1),

    RING(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING1(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING2(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING3(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING4(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING5(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING6(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING7(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING8(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),
    RING9(TIER3, new Item(1040, 1), new Item(1050, 1), 15000000, 25),

    IDD(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD1(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD2(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD3(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD4(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD5(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD6(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD7(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD8(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),
    IDD9(TIER4, new Item(1042, 1), new Item(1050, 1), 250000000, 100),

    ;

    private UpgradeType type;
    private Item required, reward;
    private int cost, successRate;
    private boolean rare;

    Upgradeables(UpgradeType type, Item required, Item reward, int cost, int successRate, boolean rare) {
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.rare = rare;
    }

    Upgradeables(UpgradeType type, Item required, Item reward, int cost, int successRate) {
        this.type = type;
        this.required = required;
        this.reward = reward;
        this.cost = cost;
        this.successRate = successRate;
        this.rare = false;
    }
    
    public static ArrayList<Upgradeables> getForType(UpgradeType type){
        ArrayList<Upgradeables> upgradeablesArrayList = new ArrayList<>();
        for (Upgradeables upgradeables : values()){
            if (upgradeables.getType() == type){
                upgradeablesArrayList.add(upgradeables);
            }
        }
        return upgradeablesArrayList;
    }
    

    public enum UpgradeType{

        TIER1, TIER2, TIER3, TIER4;

    }


}
