<%@ tag description="Stock meter + availability status line" pageEncoding="UTF-8" %>
<%@ attribute name="product" required="true" type="com.trainingdepot.gear.model.Product" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="stock" value="${product.stockQuantity}" />
<c:choose>
  <c:when test="${stock >= 20}"><c:set var="pct" value="100" /></c:when>
  <c:otherwise><c:set var="pct" value="${stock * 5}" /></c:otherwise>
</c:choose>

<div class="stock-meter">
  <div class="stock-meter__bar">
    <div class="stock-meter__fill ${product.lowStock ? 'is-low' : ''}" style="width:${pct}%;"></div>
  </div>
  <span class="stock-meter__label mono">${stock}</span>
</div>
<span class="status-line">
  <c:choose>
    <c:when test="${stock == 0}">
      <span class="status-dot is-out"></span>OUT OF STOCK
    </c:when>
    <c:when test="${product.lowStock}">
      <span class="status-dot is-low"></span>LOW STOCK
    </c:when>
    <c:otherwise>
      <span class="status-dot"></span>AVAILABLE
    </c:otherwise>
  </c:choose>
</span>
