package androidx.webkit.internal;

import androidx.webkit.UserAgentMetadata;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class UserAgentMetadataInternal {
    private static final String ARCHITECTURE = "ARCHITECTURE";
    private static final String BITNESS = "BITNESS";
    private static final int BRAND_VERSION_LENGTH = 3;
    private static final String BRAND_VERSION_LIST = "BRAND_VERSION_LIST";
    private static final String FULL_VERSION = "FULL_VERSION";
    private static final String MOBILE = "MOBILE";
    private static final String MODEL = "MODEL";
    private static final String PLATFORM = "PLATFORM";
    private static final String PLATFORM_VERSION = "PLATFORM_VERSION";
    private static final String WOW64 = "WOW64";

    static Map<String, Object> convertUserAgentMetadataToMap(UserAgentMetadata uaMetadata) {
        Map<String, Object> item = new HashMap<>();
        item.put(BRAND_VERSION_LIST, getBrandVersionArray(uaMetadata.getBrandVersionList()));
        item.put(FULL_VERSION, uaMetadata.getFullVersion());
        item.put(PLATFORM, uaMetadata.getPlatform());
        item.put(PLATFORM_VERSION, uaMetadata.getPlatformVersion());
        item.put(ARCHITECTURE, uaMetadata.getArchitecture());
        item.put(MODEL, uaMetadata.getModel());
        item.put(MOBILE, Boolean.valueOf(uaMetadata.isMobile()));
        item.put(BITNESS, Integer.valueOf(uaMetadata.getBitness()));
        item.put(WOW64, Boolean.valueOf(uaMetadata.isWow64()));
        return item;
    }

    private static String[][] getBrandVersionArray(List<UserAgentMetadata.BrandVersion> brandVersionList) {
        if (brandVersionList == null || brandVersionList.isEmpty()) {
            return null;
        }
        String[][] brandVersionArray = (String[][]) Array.newInstance((Class<?>) String.class, brandVersionList.size(), 3);
        for (int i = 0; i < brandVersionList.size(); i++) {
            brandVersionArray[i][0] = brandVersionList.get(i).getBrand();
            brandVersionArray[i][1] = brandVersionList.get(i).getMajorVersion();
            brandVersionArray[i][2] = brandVersionList.get(i).getFullVersion();
        }
        return brandVersionArray;
    }

    static UserAgentMetadata getUserAgentMetadataFromMap(Map<String, Object> uaMetadataMap) {
        UserAgentMetadata.Builder builder = new UserAgentMetadata.Builder();
        Object brandVersionValue = uaMetadataMap.get(BRAND_VERSION_LIST);
        if (brandVersionValue != null) {
            String[][] overrideBrandVersionList = (String[][]) brandVersionValue;
            List<UserAgentMetadata.BrandVersion> brandVersionList = new ArrayList<>();
            for (String[] brandVersionInfo : overrideBrandVersionList) {
                brandVersionList.add(new UserAgentMetadata.BrandVersion.Builder().setBrand(brandVersionInfo[0]).setMajorVersion(brandVersionInfo[1]).setFullVersion(brandVersionInfo[2]).build());
            }
            builder.setBrandVersionList(brandVersionList);
        }
        String fullVersion = (String) uaMetadataMap.get(FULL_VERSION);
        if (fullVersion != null) {
            builder.setFullVersion(fullVersion);
        }
        String platform = (String) uaMetadataMap.get(PLATFORM);
        if (platform != null) {
            builder.setPlatform(platform);
        }
        String platformVersion = (String) uaMetadataMap.get(PLATFORM_VERSION);
        if (platformVersion != null) {
            builder.setPlatformVersion(platformVersion);
        }
        String architecture = (String) uaMetadataMap.get(ARCHITECTURE);
        if (architecture != null) {
            builder.setArchitecture(architecture);
        }
        String model = (String) uaMetadataMap.get(MODEL);
        if (model != null) {
            builder.setModel(model);
        }
        Boolean isMobile = (Boolean) uaMetadataMap.get(MOBILE);
        if (isMobile != null) {
            builder.setMobile(isMobile.booleanValue());
        }
        Integer bitness = (Integer) uaMetadataMap.get(BITNESS);
        if (bitness != null) {
            builder.setBitness(bitness.intValue());
        }
        Boolean isWow64 = (Boolean) uaMetadataMap.get(WOW64);
        if (isWow64 != null) {
            builder.setWow64(isWow64.booleanValue());
        }
        return builder.build();
    }
}
