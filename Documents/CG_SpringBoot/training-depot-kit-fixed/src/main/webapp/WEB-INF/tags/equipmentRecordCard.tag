<%@ tag description="Equipment record card" pageEncoding="UTF-8" %>
<%@ attribute name="product" required="true" type="com.trainingdepot.gear.model.Product" %>
<%@ attribute name="showCompare" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>

<article class="equipment-record" data-product-id="${product.id}">
  <div class="equipment-record__head">
    <span class="equipment-record__number mono">${product.recordNumber}</span>
    <span class="equipment-record__category">${product.category.label}</span>
  </div>

  <a href="${pageContext.request.contextPath}/equipment/${product.id}" class="equipment-record__glyph">
    <depot:equipmentGlyph category="${product.category}" />
  </a>

  <h3 class="equipment-record__name">
    <a href="${pageContext.request.contextPath}/equipment/${product.id}">${product.name}</a>
  </h3>

  <table class="equipment-record__specs">
    <c:forEach items="${product.specifications}" var="entry" end="2" varStatus="st">
      <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
    </c:forEach>
  </table>

  <depot:stockMeter product="${product}" />

  <div class="equipment-record__price">
    <span class="now mono">&#8377;${product.finalPrice}</span>
    <c:if test="${product.discountPercent > 0}">
      <span class="was mono">&#8377;${product.price}</span>
    </c:if>
  </div>

  <div class="equipment-record__actions">
    <form method="post" action="${pageContext.request.contextPath}/kit/add" class="btn--block">
      <input type="hidden" name="productId" value="${product.id}" />
      <input type="hidden" name="quantity" value="1" />
      <input type="hidden" name="redirectTo" value="/equipment" />
      <button type="submit" class="btn btn--primary btn--block" ${product.stockQuantity == 0 ? 'disabled' : ''}>
        + ADD TO KIT
      </button>
    </form>
  </div>

  <c:if test="${showCompare == null || showCompare}">
    <label class="equipment-record__compare">
      <input type="checkbox" class="compare-check" value="${product.id}" /> COMPARE
    </label>
  </c:if>
</article>