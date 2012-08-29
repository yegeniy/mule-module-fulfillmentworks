package org.mule.module.fulfillmentworks;

import com.fulfillmentworks.api.ArrayOfCustomCategory;
import com.fulfillmentworks.api.ArrayOfOfferSort;
import com.fulfillmentworks.api.ArrayOfOfferSortGroup;

import javax.print.DocFlavor;

public class OfferSearch
{
     //Wild card order search looks like this:
     /*
         <sortGroups />
         <categoryGroupDescription />
         <customCategories />
         <mailerUID />
         <searchString>%</searchString>
         <searchID>true</searchID>
         <searchDescription>true</searchDescription>
         <priceClassDescription />
     */

    private ArrayOfOfferSortGroup mOfferSortArray;
    private String mCategoryGroupDescription;
    private ArrayOfCustomCategory mCustomCategoryArray;
    private String mMailerUID;
    private String mSearchString;
    private boolean mbSearchID;
    private boolean mbSearchDescription;
    private String mPriceClassDescription;

    public OfferSearch()
    {
        mOfferSortArray = new ArrayOfOfferSortGroup();
        mCategoryGroupDescription = "";
        mCustomCategoryArray = new ArrayOfCustomCategory();
        mMailerUID = "";
        mSearchString = "%";
        mbSearchID = true;
        mbSearchDescription = true;
        mPriceClassDescription = "";
    }

    public OfferSearch setSorting(ArrayOfOfferSortGroup sortArray)
    {
        this.mOfferSortArray = sortArray;
        return this;
    }

    public ArrayOfOfferSortGroup getSorting()
    {
        return this.mOfferSortArray;
    }

    public OfferSearch setGroupDescription(String desc)
    {
        this.mCategoryGroupDescription = desc;
        return this;
    }

    public String getGroupDescription()
    {
        return this.mCategoryGroupDescription;
    }

    public OfferSearch setCategories(ArrayOfCustomCategory catArray)
    {
        this.mCustomCategoryArray = catArray;
        return this;
    }

    public ArrayOfCustomCategory getCategories()
    {
        return this.mCustomCategoryArray;
    }

    public OfferSearch setMailerUID(String mailerUID)
    {
        this.mMailerUID = mailerUID;
        return this;
    }

    public String getMailerUID()
    {
        return this.mMailerUID;
    }


    public OfferSearch setSearchString(String searchString)
    {
        this.mSearchString = searchString;
        return this;
    }

    public String getSearchString()
    {
        return this.mSearchString;
    }

    public OfferSearch setSearchID(boolean bSearchID)
    {
        this.mbSearchID = bSearchID;
        return this;
    }

    public boolean getSearchID()
    {
        return this.mbSearchID;
    }

    public OfferSearch setSearchDesc(boolean bSearchDesc)
    {
        this.mbSearchDescription = bSearchDesc;
        return this;
    }

    public boolean getSearchDesc()
    {
        return this.mbSearchDescription;
    }

    public OfferSearch setPriceClassDesc(String priceClassDescription)
    {
        this.mPriceClassDescription =  priceClassDescription;
        return this;
    }

    public String getPriceClassDes()
    {
        return this.mPriceClassDescription;
    }
}