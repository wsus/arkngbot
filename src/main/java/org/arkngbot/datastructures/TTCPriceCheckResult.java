package org.arkngbot.datastructures;

import java.io.Serializable;

public class TTCPriceCheckResult implements Serializable {

    private PriceCheckPageModel priceCheckPageModel;
    private boolean isSuccess;
    private int code;

    public PriceCheckPageModel getPriceCheckPageModel() {
        return priceCheckPageModel;
    }

    public void setPriceCheckPageModel(PriceCheckPageModel priceCheckPageModel) {
        this.priceCheckPageModel = priceCheckPageModel;
    }

    public boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(boolean success) {
        this.isSuccess = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public static class PriceCheckPageModel implements Serializable {

        private ItemDetailPricePair[] itemDetailPricePairs;
        private int currentPage;
        private int totalPageCount;
        private int totalMatchCount;

        public ItemDetailPricePair[] getItemDetailPricePairs() {
            return itemDetailPricePairs;
        }

        public void setItemDetailPricePairs(ItemDetailPricePair[] itemDetailPricePairs) {
            this.itemDetailPricePairs = itemDetailPricePairs;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPageCount() {
            return totalPageCount;
        }

        public void setTotalPageCount(int totalPageCount) {
            this.totalPageCount = totalPageCount;
        }

        public int getTotalMatchCount() {
            return totalMatchCount;
        }

        public void setTotalMatchCount(int totalMatchCount) {
            this.totalMatchCount = totalMatchCount;
        }
    }

    public static class ItemDetailPricePair implements Serializable {
        private ItemDetail itemDetail;
        private ItemPrice itemPrice;

        public ItemDetail getItemDetail() {
            return itemDetail;
        }

        public void setItemDetail(ItemDetail itemDetail) {
            this.itemDetail = itemDetail;
        }

        public ItemPrice getItemPrice() {
            return itemPrice;
        }

        public void setItemPrice(ItemPrice itemPrice) {
            this.itemPrice = itemPrice;
        }

    }

    public static class ItemDetail implements Serializable {
        private Integer category2Id;
        private String name;
        private String iconName;
        private Integer id;
        private String uid;
        private Integer qualityId;
        private String category2IdOverWrite;
        private Integer traitId;
        private Integer[] potionEffectIds;
        private MasterWritInfo masterWritInfo;
        private Integer levelTotal;

        public Integer getCategory2Id() {
            return category2Id;
        }

        public void setCategory2Id(Integer category2Id) {
            this.category2Id = category2Id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIconName() {
            return iconName;
        }

        public void setIconName(String iconName) {
            this.iconName = iconName;
        }

        public Integer getLevelTotal() {
            return levelTotal;
        }

        public void setLevelTotal(Integer levelTotal) {
            this.levelTotal = levelTotal;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public Integer getQualityId() {
            return qualityId;
        }

        public void setQualityId(Integer qualityId) {
            this.qualityId = qualityId;
        }

        public String getCategory2IdOverWrite() {
            return category2IdOverWrite;
        }

        public void setCategory2IdOverWrite(String category2IdOverWrite) {
            this.category2IdOverWrite = category2IdOverWrite;
        }

        public Integer getTraitId() {
            return traitId;
        }

        public void setTraitId(Integer traitId) {
            this.traitId = traitId;
        }

        public Integer[] getPotionEffectIds() {
            return potionEffectIds;
        }

        public void setPotionEffectIds(Integer[] potionEffectIds) {
            this.potionEffectIds = potionEffectIds;
        }

        public MasterWritInfo getMasterWritInfo() {
            return masterWritInfo;
        }

        public void setMasterWritInfo(MasterWritInfo masterWritInfo) {
            this.masterWritInfo = masterWritInfo;
        }
    }

    public static class ItemPrice implements Serializable {
        private int priceMax;
        private int priceMin;
        private double priceAvg;
        private Double suggestedPrice;
        private Integer entryCount;
        private Integer amountCount;

        public Double getSuggestedPrice() {
            return suggestedPrice;
        }

        public void setSuggestedPrice(Double suggestedPrice) {
            this.suggestedPrice = suggestedPrice;
        }

        public Integer getEntryCount() {
            return entryCount;
        }

        public void setEntryCount(Integer entryCount) {
            this.entryCount = entryCount;
        }

        public Integer getAmountCount() {
            return amountCount;
        }

        public void setAmountCount(Integer amountCount) {
            this.amountCount = amountCount;
        }

        public int getPriceMax() {
            return priceMax;
        }

        public void setPriceMax(int priceMax) {
            this.priceMax = priceMax;
        }

        public int getPriceMin() {
            return priceMin;
        }

        public void setPriceMin(int priceMin) {
            this.priceMin = priceMin;
        }

        public double getPriceAvg() {
            return priceAvg;
        }

        public void setPriceAvg(double priceAvg) {
            this.priceAvg = priceAvg;
        }
    }

    public static class MasterWritInfo implements Serializable {
        private String requiredItemName;
        private String requiredItemId;
        private String requiredQualityId;
        private String requiredTraitId;
        private String requiredSetID;
        private String requiredStyleID;
        private String[] requiredPotionEffectIDs;
        private String numVoucher;
        private Boolean isEmpty;

        public String getRequiredItemName() {
            return requiredItemName;
        }

        public void setRequiredItemName(String requiredItemName) {
            this.requiredItemName = requiredItemName;
        }

        public String getRequiredItemId() {
            return requiredItemId;
        }

        public void setRequiredItemId(String requiredItemId) {
            this.requiredItemId = requiredItemId;
        }

        public String getRequiredQualityId() {
            return requiredQualityId;
        }

        public void setRequiredQualityId(String requiredQualityId) {
            this.requiredQualityId = requiredQualityId;
        }

        public String getRequiredTraitId() {
            return requiredTraitId;
        }

        public void setRequiredTraitId(String requiredTraitId) {
            this.requiredTraitId = requiredTraitId;
        }

        public String getRequiredSetID() {
            return requiredSetID;
        }

        public void setRequiredSetID(String requiredSetID) {
            this.requiredSetID = requiredSetID;
        }

        public String getRequiredStyleID() {
            return requiredStyleID;
        }

        public void setRequiredStyleID(String requiredStyleID) {
            this.requiredStyleID = requiredStyleID;
        }

        public String[] getRequiredPotionEffectIDs() {
            return requiredPotionEffectIDs;
        }

        public void setRequiredPotionEffectIDs(String[] requiredPotionEffectIDs) {
            this.requiredPotionEffectIDs = requiredPotionEffectIDs;
        }

        public String getNumVoucher() {
            return numVoucher;
        }

        public void setNumVoucher(String numVoucher) {
            this.numVoucher = numVoucher;
        }

        public Boolean getIsEmpty() {
            return isEmpty;
        }

        public void setIsEmpty(Boolean empty) {
            isEmpty = empty;
        }
    }
}
