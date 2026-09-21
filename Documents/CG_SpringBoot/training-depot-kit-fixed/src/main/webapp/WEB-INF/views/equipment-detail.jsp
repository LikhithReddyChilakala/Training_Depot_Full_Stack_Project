<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="${product.name}" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-aside">
    <div class="depot-panel-card" style="text-align:center;">
      <div class="equipment-record__glyph" style="height:160px; color: var(--depot-graphite-dark);">
        <depot:equipmentGlyph category="${product.category}" />
      </div>
    </div>
  </div>

  <section class="shell-main--wide">
    <span class="eyebrow">${product.recordNumber} &nbsp;&middot;&nbsp; ${product.category.label}</span>
    <h1>${product.name}</h1>
    <p style="color: var(--depot-graphite-dark);">${product.description}</p>

    <table class="equipment-record__specs" style="max-width:480px; margin-bottom: var(--space-4);">
      <c:forEach items="${product.specifications}" var="entry">
        <tr><td>${entry.key}</td><td>${entry.value}</td></tr>
      </c:forEach>
    </table>

    <depot:stockMeter product="${product}" />

    <div class="equipment-record__price" style="margin: var(--space-4) 0;">
      <span class="now mono" style="font-size:28px;">&#8377;${product.finalPrice}</span>
      <c:if test="${product.discountPercent > 0}">
        <span class="was mono">&#8377;${product.price}</span>
        <span class="tag tag--confirmed">${product.discountPercent}% OFF</span>
      </c:if>
    </div>

    <form method="post" action="${ctx}/kit/add" style="display:flex; align-items:center; gap: var(--space-3);">
      <input type="hidden" name="productId" value="${product.id}" />
      <input type="hidden" name="redirectTo" value="${ctx}/equipment/${product.id}" />
      <div class="qty-stepper" data-auto-submit="false">
        <button type="button" class="qty-decrease" aria-label="Decrease quantity">&minus;</button>
        <input type="number" name="quantity" value="1" min="1" max="${product.stockQuantity}" />
        <button type="button" class="qty-increase" aria-label="Increase quantity">+</button>
      </div>
      <button type="submit" class="btn btn--primary" ${product.stockQuantity == 0 ? 'disabled' : ''}>+ ADD TO KIT</button>
    </form>

    <p style="margin-top: var(--space-5);"><a href="${ctx}/equipment">&larr; BACK TO EQUIPMENT INDEX</a></p>
  </section>
</main>

<script src="${ctx}/js/kit.js"></script>
<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
