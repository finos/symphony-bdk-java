package com.symphony.bdk.core.service.user.constant;

import org.apiguardian.api.API;

/**
 * Static roles that have special logic attached to them.
 * @see <a href="https://developers.symphony.com/restapi/reference/roles-object">Symphony Roles</a>
 */
@API(status = API.Status.STABLE)
public enum RoleId {
  ADMINISTRATOR,
  SUPER_ADMINISTRATOR,
  COMPLIANCE_OFFICER,
  SUPER_COMPLIANCE_OFFICER,
  L1_SUPPORT,
  L2_SUPPORT,
  USER_PROVISIONING,
  CONTENT_EXPORT_SERVICE,
  INDIVIDUAL,
  SYMPHONY_ADMIN,
  KEY_MANAGER,
  CONTENT_MANAGEMENT,
  EF_POLICY_MANAGEMENT,
  MALWARE_SCAN_MANAGER,
  MALWARE_SCAN_STATE_USER,
  AGENT,
  SCOPE_MANAGEMENT,
  AUDIT_TRAIL_MANAGEMENT,
  CEP_VISIBILITY_GROUP_MANAGEMENT
}
