package nl.wouterdebruijn.slurp.helper;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public enum Permissions {
    SLURP_DRINKER("slurp.drinker", PermissionDefault.TRUE),
    SLURP_ADMIN("slurp.admin", PermissionDefault.OP);


    private final String permission;
    private final PermissionDefault permissionDefault;

    Permissions(String permission, PermissionDefault permissionDefault) {
        this.permission = permission;
        this.permissionDefault = permissionDefault;
    }

    public Permission getBukkitPermission() {
        return new Permission(permission, permissionDefault);
    }
}
