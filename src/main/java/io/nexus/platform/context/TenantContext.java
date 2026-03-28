package io.nexus.platform.context;

public class TenantContext {

    private static final ThreadLocal<String> currentTenant = new ThreadLocal<>();

    public static void setTenantSlug(String tenantSlug) {
        currentTenant.set(tenantSlug);
    }

    public static String getTenantSlug() {
        return currentTenant.get();
    }

    public static void clear() {
        currentTenant.remove();
    }
}