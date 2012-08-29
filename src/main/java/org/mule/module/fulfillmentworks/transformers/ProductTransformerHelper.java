package org.mule.module.fulfillmentworks.transformers;

import java.math.BigDecimal;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.fulfillmentworks.api.AcquisitionType;
import com.fulfillmentworks.api.ArrayOfOfferCategory;
import com.fulfillmentworks.api.ArrayOfOfferComponent;
import com.fulfillmentworks.api.ArrayOfProductActivation;
import com.fulfillmentworks.api.ArrayOfProductBillFactor;
import com.fulfillmentworks.api.ArrayOfProductWMSSystem;
import com.fulfillmentworks.api.BackorderTreatment;
import com.fulfillmentworks.api.BillOfMaterials;
import com.fulfillmentworks.api.BuildType;
import com.fulfillmentworks.api.CostCenter;
import com.fulfillmentworks.api.CountFrequency;
import com.fulfillmentworks.api.CustomCategory;
import com.fulfillmentworks.api.CustomCategoryDef;
import com.fulfillmentworks.api.OMSSystem;
import com.fulfillmentworks.api.Offer;
import com.fulfillmentworks.api.OfferCategorization;
import com.fulfillmentworks.api.OfferCategory;
import com.fulfillmentworks.api.OfferComponent;
import com.fulfillmentworks.api.OfferCustomization;
import com.fulfillmentworks.api.OfferDropShip;
import com.fulfillmentworks.api.OfferHeader;
import com.fulfillmentworks.api.OfferImages;
import com.fulfillmentworks.api.OfferInfo;
import com.fulfillmentworks.api.OfferPriceType;
import com.fulfillmentworks.api.OfferPricing;
import com.fulfillmentworks.api.OfferRecurrence;
import com.fulfillmentworks.api.OfferSettings;
import com.fulfillmentworks.api.OfferStatus;
import com.fulfillmentworks.api.PMSystem;
import com.fulfillmentworks.api.PackTrack;
import com.fulfillmentworks.api.PriceType;
import com.fulfillmentworks.api.Product;
import com.fulfillmentworks.api.ProductAcquisition;
import com.fulfillmentworks.api.ProductActivation;
import com.fulfillmentworks.api.ProductBillFactor;
import com.fulfillmentworks.api.ProductCharacteristics;
import com.fulfillmentworks.api.ProductHeader;
import com.fulfillmentworks.api.ProductID;
import com.fulfillmentworks.api.ProductIDHeader;
import com.fulfillmentworks.api.ProductOptional;
import com.fulfillmentworks.api.ProductSN;
import com.fulfillmentworks.api.ProductSort;
import com.fulfillmentworks.api.ProductType;
import com.fulfillmentworks.api.ProductValuation;
import com.fulfillmentworks.api.ProductWMSSystem;
import com.fulfillmentworks.api.ReceiptValution;
import com.fulfillmentworks.api.RecurrenceType;
import com.fulfillmentworks.api.RemotePriceTreatment;
import com.fulfillmentworks.api.ReturnTreatment;
import com.fulfillmentworks.api.SerialNumbers;
import com.fulfillmentworks.api.ShippingAndHandlingChargeType;
import com.fulfillmentworks.api.UnitOfMeasure;
import com.fulfillmentworks.api.UsageCode;
import com.fulfillmentworks.api.WeightType;

@ManagedResource
public class ProductTransformerHelper {

    private String systemID;

    private int seqID;
    
    private String ownerID;
    
    private int bundleProductId;
    private int simpleProductId;

    private BackorderTreatment bundleProductBackorderTreatment = BackorderTreatment.BACKORDER_ENTIRE_OFFER_LINE;
    private BackorderTreatment simpleProductBackorderTreatment = BackorderTreatment.SYSTEM_DEFAULT;
    private Boolean bundleProductDoNotShipAlone = Boolean.FALSE;
    private Boolean simpleProductDoNotShipAlone = Boolean.FALSE;
    
    @ManagedAttribute
    public String getOwnerID() {
        return ownerID;
    }
    
    public int getOwnerIDAsInteger() {
        return Integer.parseInt((this.getOwnerID()));
    }

    @ManagedAttribute
    public void setOwnerID(String ownerID) {
        this.ownerID = ownerID;
    }

    @ManagedAttribute
    public int getSeqID()
    {
        return seqID;
    }

    @ManagedAttribute
    public void setSeqID(int seqID)
    {
        this.seqID = seqID;
    }

    @ManagedAttribute
    public String getSystemID()
    {
        return systemID;
    }

    @ManagedAttribute
    public void setSystemID(String systemID)
    {
        this.systemID = systemID;
    }
    
    public void setBundleProductId(int bundleProductId)
    {
        this.bundleProductId = bundleProductId;
    }
    
    public void setSimpleProductId(int simpleProductId)
    {
        this.simpleProductId = simpleProductId;
    }
    
    public void setBundleProductBackorderTreatment(BackorderTreatment bundleProductBackorderTreatment)
    {
        this.bundleProductBackorderTreatment = bundleProductBackorderTreatment;
    }
    
    public void setSimpleProductBackorderTreatment(BackorderTreatment simpleProductBackorderTreatment)
    {
        this.simpleProductBackorderTreatment = simpleProductBackorderTreatment;
    }

    public void setBundleProductDoNotShipAlone(Boolean bundleProductDoNotShipAlone) {
        this.bundleProductDoNotShipAlone = bundleProductDoNotShipAlone;
    }

    public void setSimpleProductDoNotShipAlone(Boolean simpleProductDoNotShipAlone) {
        this.simpleProductDoNotShipAlone = simpleProductDoNotShipAlone;
    }

    public Offer getTemplateSimpleOffer()
    {
        //Setup the offer
        Offer offer = new Offer();

        //Header
        OfferHeader offerHeader = new OfferHeader();
        offerHeader.setID("ID");
        offerHeader.setDescription("Test Offer Description 1");
        offer.setHeader(offerHeader);

        //Info
        OfferInfo offerInfo = new OfferInfo();
        offerInfo.setBillOfMaterials(BillOfMaterials.PRODUCT_LIST);
        offerInfo.setCustomAssembly(false);
        offer.setInfo(offerInfo);

        //Status
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setInactive(false);
        offer.setStatus(offerStatus);

        //Settings
        OfferSettings offerSettings = new OfferSettings();
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setSeqID(this.getSeqID());
        unitOfMeasure.setNeedsRemoval(false);
        unitOfMeasure.setWasModified(false);
        offerSettings.setUnitOfMeasure(unitOfMeasure);
        offerSettings.setShipSeperately(false);
        offerSettings.setNoShCharges(false);
        offerSettings.setNoShFields(false);
        offer.setSettings(offerSettings);

        //Pricing
        OfferPricing offerPricing = new OfferPricing();
        offerPricing.setPriceType(OfferPriceType.EACH);
        offerPricing.setShippingAndHandlingChargeType(ShippingAndHandlingChargeType.EACH);
        offerPricing.setTaxable(false);
        offerPricing.setClusterSurcharge(false);
        offer.setPricing(offerPricing);
        
        OfferRecurrence offerRecurrence = new OfferRecurrence();
        offerRecurrence.setRecurrence(RecurrenceType.NONE);
        offer.setRecurrence(offerRecurrence);

        //Customization
        OfferCustomization offerCustomization = new OfferCustomization();
        offerCustomization.setRemoteOnly(false);
        offerCustomization.setRemotePriceTreatment(RemotePriceTreatment.OFFER_PRICING);
        offerCustomization.setPreviewButton(false);
        offer.setCustomization(offerCustomization);

        //Images
        OfferImages offerImages = new OfferImages();
        offerImages.setLocalImages(false);
        offer.setImages(offerImages);

        //Dropship
        OfferDropShip offerDropShip = new OfferDropShip();
        offerDropShip.setWeightType(WeightType.OZ);
        offer.setDropShip(offerDropShip);
        
        //Custom Catagories
        CustomCategoryDef customCategoryDef = new CustomCategoryDef();
        CustomCategory customCategory = new CustomCategory();
        customCategory.setCategoryDef(customCategoryDef);
        customCategory.setSeqID(simpleProductId);
        OfferCategory offerCategory = new OfferCategory();
        offerCategory.setCategory(customCategory);
        ArrayOfOfferCategory catArr = new ArrayOfOfferCategory();
        catArr.getOfferCategory().add(offerCategory);
        offer.setCategories(catArr);

        //Offer components
        ArrayOfOfferComponent arrayOfOfferComponent = new ArrayOfOfferComponent();
        OfferComponent offerComponent = new OfferComponent();

        ProductIDHeader productIDHeader = new ProductIDHeader();
        productIDHeader.setPartNumber("ID");
        com.fulfillmentworks.api.Owner owner = new com.fulfillmentworks.api.Owner();
        owner.setSeqID(this.getOwnerIDAsInteger());

        productIDHeader.setOwner(owner);
        ProductID productID = new ProductID();
        productID.setHeader(productIDHeader);
        offerComponent.setProduct(productID);

        offerComponent.setQuantity(1);
        offerComponent.setBackorderTreatment(simpleProductBackorderTreatment);
        offerComponent.setIsPrimaryRevenue(true);
        offerComponent.setRevenueAmount(BigDecimal.ZERO);
        offerComponent.setExcludeFromAvailability(false);
        offerComponent.setDoNotShipAlone(simpleProductDoNotShipAlone);

        arrayOfOfferComponent.getOfferComponent().add(offerComponent);

        offer.setComponents(arrayOfOfferComponent);

        return offer;

    }

    //This is only used for bundles
     public OfferComponent generateTemplateOfferComponent()
     {


        OfferComponent offerComponent = new OfferComponent();

        ProductIDHeader productIDHeader = new ProductIDHeader();

        //Leaving this in, but commented out in case we ever need it..
        //productIDHeader.setSeqID(this.getSeqID());

//      THis might be needed at some point
        com.fulfillmentworks.api.Owner owner = new com.fulfillmentworks.api.Owner();
        owner.setSeqID(this.getOwnerIDAsInteger());
        productIDHeader.setOwner(owner);
        ProductID productID = new ProductID();
        productID.setHeader(productIDHeader);
        offerComponent.setProduct(productID);

        offerComponent.setQuantity(1);
        //Since this is only used for bundles, it gets a different backorder treatment
        offerComponent.setBackorderTreatment(bundleProductBackorderTreatment);
        offerComponent.setIsPrimaryRevenue(true);
        offerComponent.setRevenueAmount(BigDecimal.ZERO);
        offerComponent.setExcludeFromAvailability(false);
        offerComponent.setDoNotShipAlone(bundleProductDoNotShipAlone);

         return offerComponent;
     }

    public Offer getTemplateBundleOffer()
    {
         //Setup the offer
        Offer offer = new Offer();


        //Header
        OfferHeader offerHeader = new OfferHeader();
        offerHeader.setID("TempID");
        offerHeader.setDescription("Test bumdle Offer Description 1");
        offer.setHeader(offerHeader);

        //Info
        OfferInfo offerInfo = new OfferInfo();
        offerInfo.setBillOfMaterials(BillOfMaterials.PRODUCT_LIST);
        offerInfo.setCustomAssembly(false);
        offer.setInfo(offerInfo);

        //Status
        OfferStatus offerStatus = new OfferStatus();
        offerStatus.setInactive(false);
        offer.setStatus(offerStatus);

        //Settings
        OfferSettings offerSettings = new OfferSettings();
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setSeqID(this.getSeqID());
        unitOfMeasure.setNeedsRemoval(false);
        unitOfMeasure.setWasModified(false);
        offerSettings.setUnitOfMeasure(unitOfMeasure);
        offerSettings.setShipSeperately(false);
        offerSettings.setNoShCharges(false);
        offerSettings.setNoShFields(false);
        offer.setSettings(offerSettings);

        //Pricing
        OfferPricing offerPricing = new OfferPricing();
        offerPricing.setPriceType(OfferPriceType.EACH);
        offerPricing.setShippingAndHandlingChargeType(ShippingAndHandlingChargeType.EACH);
        offerPricing.setTaxable(false);
        offerPricing.setClusterSurcharge(false);
        offer.setPricing(offerPricing);
        
        OfferRecurrence offerRecurrence = new OfferRecurrence();
        offerRecurrence.setRecurrence(RecurrenceType.NONE);
        offer.setRecurrence(offerRecurrence);

        //Customization
        OfferCustomization offerCustomization = new OfferCustomization();
        offerCustomization.setRemoteOnly(false);
        offerCustomization.setRemotePriceTreatment(RemotePriceTreatment.OFFER_PRICING);
        offerCustomization.setPreviewButton(false);
        offer.setCustomization(offerCustomization);
        

        //Images
        OfferImages offerImages = new OfferImages();
        offerImages.setLocalImages(false);
        offer.setImages(offerImages);

        //Dropship
        OfferDropShip offerDropShip = new OfferDropShip();
        offerDropShip.setWeightType(WeightType.OZ);
        offer.setDropShip(offerDropShip);
        
      //Custom Catagories
        CustomCategoryDef customCategoryDef = new CustomCategoryDef();
        CustomCategory customCategory = new CustomCategory();
        customCategory.setCategoryDef(customCategoryDef);
        customCategory.setSeqID(bundleProductId);
        OfferCategory offerCategory = new OfferCategory();
        offerCategory.setCategory(customCategory);
        ArrayOfOfferCategory catArr = new ArrayOfOfferCategory();
        catArr.getOfferCategory().add(offerCategory);
        offer.setCategories(catArr);
        
        //Offer components
        ArrayOfOfferComponent arrayOfOfferComponent = new ArrayOfOfferComponent();
        OfferComponent offerComponent = generateTemplateOfferComponent();
        arrayOfOfferComponent.getOfferComponent().add(offerComponent);
        offer.setComponents(arrayOfOfferComponent);

        return offer;
    }


    public Product getTemplateProduct()
    {
        //Set up the product
        Product product = new Product();

        //Product Header
        ProductHeader productHeader = new ProductHeader();
        productHeader.setPartNumber("ID");
        productHeader.setDescription("Test Description 1");
        productHeader.setBuildType(BuildType.PRODUCT);
        productHeader.setUsageCode(UsageCode.EXCLUSIVE_TO_OWNER);
        productHeader.setOfferFlag(true);

        com.fulfillmentworks.api.Owner owner = new com.fulfillmentworks.api.Owner();
        owner.setSeqID(this.getOwnerIDAsInteger());

        productHeader.setOwner(owner);

        CostCenter costCenter = new CostCenter();
        costCenter.setRemovable(false);
        productHeader.setCostCenter(costCenter);

        product.setHeader(productHeader);


        //Product valuation
        ProductValuation productValuation = new ProductValuation();
        productValuation.setReceiptValution(ReceiptValution.fromValue("Manual"));
        product.setValuation(productValuation);


        //Product Acquisition
        ProductAcquisition productAcquisition = new ProductAcquisition();
        productAcquisition.setAcquisitionType(AcquisitionType.UNKNOWN);
        product.setAcquisition(productAcquisition);

        //Warehouse system
        PMSystem pmSystem = new PMSystem();
        pmSystem.setID(this.getSystemID());

        ProductWMSSystem productWMSSystem = new ProductWMSSystem();
        productWMSSystem.setSystem(pmSystem);
        productWMSSystem.setActive(true);
        productWMSSystem.setCountFrequency(CountFrequency.ON_DEMAND);
        productWMSSystem.setIsDefault(true);

        ArrayOfProductWMSSystem arrayOfProductWMSSystem = new ArrayOfProductWMSSystem();
        arrayOfProductWMSSystem.getProductWMSSystem().add(productWMSSystem);
        product.setWarehouseSystems(arrayOfProductWMSSystem);

        //Serial Numbers
        ProductSN productSN = new ProductSN();
        productSN.setSerialNumbers(SerialNumbers.NO_SERIAL_NUMBERS);
        product.setSerialNumber(productSN);

        //Optional Info
        ProductOptional productOptional = new ProductOptional();
        productOptional.setDefaultPriceType(PriceType.EACH);
        productOptional.setReturnTreatment(ReturnTreatment.CASE_BY_CASE);
        product.setOptionalInfo(productOptional);

        //Bill Factors
        ProductBillFactor productBillFactor = new ProductBillFactor();
        productBillFactor.setFromQuantity(1);
        productBillFactor.setBillFactor(1.0);

        ArrayOfProductBillFactor arrayOfProductBillFactor = new ArrayOfProductBillFactor();
        arrayOfProductBillFactor.getProductBillFactor().add(productBillFactor);
        product.setBillFactors(arrayOfProductBillFactor);

        //Product sort
        ProductType productType = new ProductType();
        productType.setSeqID(1);
        ProductSort productSort = new ProductSort();
        productSort.setProductType(productType);
        product.setSort(productSort);

        //Characteristics
        ProductCharacteristics productCharacteristics = new ProductCharacteristics();
        productCharacteristics.setDefaultWeightType(WeightType.OZ);
        productCharacteristics.setPrePack(false);
        productCharacteristics.setPrePack(true);
        productCharacteristics.setPackTrack(PackTrack.EACH);
        productCharacteristics.setShipSeparatePackages(false);
        productCharacteristics.setImageLocal(true);
        product.setCharacteristics(productCharacteristics);

        //Default Version stuff - Might be needed later?
//        ProductVersion productVersion = new ProductVersion();
//        productVersion.setWeightType(WeightType.OZ);
//        VersionStatus versionStatus = new VersionStatus();
//        versionStatus.setSeqID(1);
//        productVersion.setStatus(versionStatus);
//        productVersion.setHasWarehouseTransactions(false);
//
//        ArrayOfProductVersion arrayOfProductVersion = new ArrayOfProductVersion();
//        arrayOfProductVersion.getProductVersion().add(productVersion);
//        product.setVersions(arrayOfProductVersion);

        //Activation
        ProductActivation productActivation = new ProductActivation();
        productActivation.setActive(true);
        OMSSystem omsSystem = new OMSSystem();
        omsSystem.setSeqID(this.getSeqID());
        productActivation.setOMSSystem(omsSystem);
        ArrayOfProductActivation arrayOfProductActivation = new ArrayOfProductActivation();
        arrayOfProductActivation.getProductActivation().add(productActivation);
        product.setActivation(arrayOfProductActivation);

        return product;
    }

    public String getFWShipperID()
    {
        return "4db5d79e3337d09b72020000";
    }

}
