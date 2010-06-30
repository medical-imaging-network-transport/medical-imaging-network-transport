package org.nema.medical.mint;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class AssocMap {
    public static class AssocInfo {
        long lastUpdateTimestamp;
        Set<File> files;
    }

    public Map<File, AssocInfo> map = new HashMap<File, AssocInfo>();
}
