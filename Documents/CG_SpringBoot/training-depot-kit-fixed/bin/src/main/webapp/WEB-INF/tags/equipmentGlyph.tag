<%@ tag description="Line-drawing equipment glyph for a category" pageEncoding="UTF-8" %>
<%@ attribute name="category" required="true" type="java.lang.Object" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="cat" value="${category}" />

<c:choose>
<c:when test="${cat == 'DUMBBELLS'}">
<svg viewBox="0 0 100 60" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <circle cx="16" cy="30" r="17"/>
  <circle cx="16" cy="30" r="8"/>
  <circle cx="84" cy="30" r="17"/>
  <circle cx="84" cy="30" r="8"/>
  <line x1="33" y1="30" x2="67" y2="30" stroke-width="6"/>
  <line x1="45" y1="21" x2="45" y2="39"/>
  <line x1="55" y1="21" x2="55" y2="39"/>
</svg>
</c:when>
<c:when test="${cat == 'KETTLEBELLS'}">
<svg viewBox="0 0 70 90" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M24 26 Q24 9 35 9 Q46 9 46 26"/>
  <circle cx="35" cy="58" r="27"/>
</svg>
</c:when>
<c:when test="${cat == 'RESISTANCE_BANDS'}">
<svg viewBox="0 0 100 60" fill="none" stroke="currentColor" stroke-width="5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M8 30 C 24 4, 38 56, 54 30 C 70 4, 84 56, 94 30"/>
</svg>
</c:when>
<c:when test="${cat == 'YOGA'}">
<svg viewBox="0 0 100 60" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <rect x="4" y="12" width="78" height="36" rx="5"/>
  <circle cx="90" cy="30" r="9"/>
  <line x1="16" y1="22" x2="70" y2="22" stroke-width="2"/>
  <line x1="16" y1="30" x2="70" y2="30" stroke-width="2"/>
  <line x1="16" y1="38" x2="70" y2="38" stroke-width="2"/>
</svg>
</c:when>
<c:when test="${cat == 'CARDIO_EQUIPMENT'}">
<svg viewBox="0 0 70 90" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <rect x="26" y="2" width="18" height="8" rx="1.5"/>
  <line x1="35" y1="10" x2="35" y2="18"/>
  <circle cx="35" cy="52" r="31"/>
  <line x1="35" y1="52" x2="35" y2="30"/>
  <line x1="35" y1="52" x2="51" y2="60"/>
</svg>
</c:when>
<c:when test="${cat == 'STRENGTH_EQUIPMENT'}">
<svg viewBox="0 0 120 50" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <line x1="22" y1="25" x2="98" y2="25" stroke-width="4"/>
  <rect x="7" y="7" width="8" height="36"/>
  <rect x="18" y="12" width="6" height="26"/>
  <rect x="105" y="7" width="8" height="36"/>
  <rect x="96" y="12" width="6" height="26"/>
</svg>
</c:when>
<c:when test="${cat == 'GYM_ACCESSORIES'}">
<svg viewBox="0 0 70 70" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <path d="M9 31 L30 10 L61 10 L61 41 L40 62 Z"/>
  <circle cx="48" cy="23" r="5"/>
</svg>
</c:when>
<c:when test="${cat == 'RECOVERY_EQUIPMENT'}">
<svg viewBox="0 0 100 40" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <rect x="9" y="4" width="82" height="32" rx="16"/>
  <line x1="26" y1="4" x2="26" y2="36" stroke-width="2"/>
  <line x1="42" y1="4" x2="42" y2="36" stroke-width="2"/>
  <line x1="58" y1="4" x2="58" y2="36" stroke-width="2"/>
  <line x1="74" y1="4" x2="74" y2="36" stroke-width="2"/>
</svg>
</c:when>
<c:otherwise>
<svg viewBox="0 0 60 60" fill="none" stroke="currentColor" stroke-width="3.5" stroke-linecap="round" stroke-linejoin="round">
  <rect x="8" y="8" width="44" height="44" rx="4"/>
</svg>
</c:otherwise>
</c:choose>
