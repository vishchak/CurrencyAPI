public class Currency {
    @Id
    private int id;

    private String baseCurrency;
    private String currency;
    private String saleRateNB;
    private String purchaseRateNB;
    private String saleRate;
    private String purchaseRate;
    private String date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getSaleRateNB() {
        return saleRateNB;
    }

    public void setSaleRateNB(String saleRateNB) {
        this.saleRateNB = saleRateNB;
    }

    public String getPurchaseRateNB() {
        return purchaseRateNB;
    }

    public void setPurchaseRateNB(String purchaseRateNB) {
        this.purchaseRateNB = purchaseRateNB;
    }

    public String getSaleRate() {
        return saleRate;
    }

    public void setSaleRate(String saleRate) {
        this.saleRate = saleRate;
    }

    public String getPurchaseRate() {
        return purchaseRate;
    }

    public void setPurchaseRate(String purchaseRate) {
        this.purchaseRate = purchaseRate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "date='" + date + '\'' +
                ", id=" + id +
                ", baseCurrency='" + baseCurrency + '\'' +
                ", currency='" + currency + '\'' +
                ", saleRateNB='" + saleRateNB + '\'' +
                ", purchaseRateNB='" + purchaseRateNB + '\'' +
                ", saleRate='" + saleRate + '\'' +
                ", purchaseRate='" + purchaseRate + '\'' +
                '}';
    }
}