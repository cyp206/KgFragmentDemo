package com.seu.magicfilter.filter;

import com.seu.magicfilter.filter.adavanced.MagicAmaroFilter;
import com.seu.magicfilter.filter.adavanced.MagicAntiqueFilter;
import com.seu.magicfilter.filter.adavanced.MagicBlackCatFilter;
import com.seu.magicfilter.filter.adavanced.MagicBrannanFilter;
import com.seu.magicfilter.filter.adavanced.MagicBrooklynFilter;
import com.seu.magicfilter.filter.adavanced.MagicCalmFilter;
import com.seu.magicfilter.filter.adavanced.MagicCoolFilter;
import com.seu.magicfilter.filter.adavanced.MagicCrayonFilter;
import com.seu.magicfilter.filter.adavanced.MagicEarlyBirdFilter;
import com.seu.magicfilter.filter.adavanced.MagicEmeraldFilter;
import com.seu.magicfilter.filter.adavanced.MagicEvergreenFilter;
import com.seu.magicfilter.filter.adavanced.MagicFreudFilter;
import com.seu.magicfilter.filter.adavanced.MagicHealthyFilter;
import com.seu.magicfilter.filter.adavanced.MagicHefeFilter;
import com.seu.magicfilter.filter.adavanced.MagicHudsonFilter;
import com.seu.magicfilter.filter.adavanced.MagicInkwellFilter;
import com.seu.magicfilter.filter.adavanced.MagicKevinFilter;
import com.seu.magicfilter.filter.adavanced.MagicLatteFilter;
import com.seu.magicfilter.filter.adavanced.MagicLomoFilter;
import com.seu.magicfilter.filter.adavanced.MagicN1977Filter;
import com.seu.magicfilter.filter.adavanced.MagicNashvilleFilter;
import com.seu.magicfilter.filter.adavanced.MagicNostalgiaFilter;
import com.seu.magicfilter.filter.adavanced.MagicPixarFilter;
import com.seu.magicfilter.filter.adavanced.MagicRiseFilter;
import com.seu.magicfilter.filter.adavanced.MagicRomanceFilter;
import com.seu.magicfilter.filter.adavanced.MagicSakuraFilter;
import com.seu.magicfilter.filter.adavanced.MagicSketchFilter;
import com.seu.magicfilter.filter.adavanced.MagicSkinWhitenFilter;
import com.seu.magicfilter.filter.base.gpuimage.GPUImageFilter;

/**
 * Created by hujinrong on 17/5/16.
 */

public class FilterFactory {
    public enum Filter {
        Amaro,
        Antique,
        BlackCat,
        Brannan,
        Brooklyn,
        Calm,
        Cool,
        Crayon,
        EarlyBird,
        EmeraId,
        Evergreen,
        Freud,
        Healthy,
        Hefe,
        Hudson,
        Inkwell,
        Kevin,
        Latte,
        Lomo,
        N1977,
        Nashville,
        Nostalgia,
        Pixar,
        Rise,
        Romance,
        Sakura,
        Sketch,
        SkinWhiten,
        NULL
    }
    public static GPUImageFilter createFilter(Filter filter) {
        GPUImageFilter imageFilter = null ;
        switch (filter) {
            case Amaro:
                imageFilter = new MagicAmaroFilter();
                break;
            case Antique:
                imageFilter = new MagicAntiqueFilter();
                break;
            case BlackCat:
                imageFilter = new MagicBlackCatFilter();
                break;
            case Brannan:
                imageFilter = new MagicBrannanFilter();
                break;
            case Brooklyn:
                imageFilter = new MagicBrooklynFilter();
                break;
            case Calm:
                imageFilter = new MagicCalmFilter();
                break;
            case Cool:
                imageFilter = new MagicCoolFilter();
                break;
            case Crayon:
                imageFilter = new MagicCrayonFilter();
                break;
            case EarlyBird:
                imageFilter = new MagicEarlyBirdFilter();
                break;
            case EmeraId:
                imageFilter = new MagicEmeraldFilter();
                break;
            case Evergreen:
                imageFilter = new MagicEvergreenFilter();
                break;
            case Freud:
                imageFilter = new MagicFreudFilter();
                break;
            case Healthy:
                imageFilter = new MagicHealthyFilter();
                break;
            case Hefe:
                imageFilter = new MagicHefeFilter();
                break;
            case Hudson:
                imageFilter = new MagicHudsonFilter();
                break;
            case Inkwell:
                imageFilter = new MagicInkwellFilter();
                break;
            case Kevin:
                imageFilter = new MagicKevinFilter();
                break;
            case Latte:
                imageFilter = new MagicLatteFilter();
                break;
            case N1977:
                imageFilter = new MagicN1977Filter();
                break;
            case Lomo:
                imageFilter = new MagicLomoFilter();
                break;
            case Nashville:
                imageFilter = new MagicNashvilleFilter();
                break;
            case Nostalgia:
                imageFilter = new MagicNostalgiaFilter();
                break;
            case Pixar:
                imageFilter = new MagicPixarFilter();
                break;
            case Rise:
                imageFilter = new MagicRiseFilter();
                break;
            case Romance:
                imageFilter = new MagicRomanceFilter();
                break;
            case Sakura:
                imageFilter = new MagicSakuraFilter();
                break;
            case Sketch:
                imageFilter = new MagicSketchFilter();
                break;
            case SkinWhiten:
                imageFilter = new MagicSkinWhitenFilter();
                break;
            case NULL:
                break;
            default:
                throw new RuntimeException("Not Support Filter.");
        }
        return imageFilter ;
    }
}
