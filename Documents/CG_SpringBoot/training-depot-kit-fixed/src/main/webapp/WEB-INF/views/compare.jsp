<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Compare Equipment" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full">
    <span class="eyebrow">SIDE-BY-SIDE</span>
    <h1>Compare Equipment</h1>

    <c:if test="${fn:length(products) < 2}">
      <div class="empty-state">
        <p>Pick at least two records from the equipment index to compare them here.</p>
        <a href="${ctx}/equipment" class="btn btn--ghost">BACK TO EQUIPMENT INDEX</a>
      </div>
    </c:if>

    <c:if test="${fn:length(products) >= 2}">
      <div class="table-scroll">
        <table class="depot-table" style="min-width:560px;">
          <thead>
            <tr>
              <th>Record</th>
              <c:forEach items="${products}" var="p">
                <th>${p.recordNumber}</th>
              </c:forEach>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td class="eyebrow">GLYPH</td>
              <c:forEach items="${products}" var="p">
                <td><div class="equipment-record__glyph" style="height:60px;"><depot:equipmentGlyph category="${p.category}" /></div></td>
              </c:forEach>
            </tr>
            <tr>
              <td class="eyebrow">NAME</td>
              <c:forEach items="${products}" var="p">
                <td><a href="${ctx}/equipment/${p.id}">${p.name}</a></td>
              </c:forEach>
            </tr>
            <tr>
              <td class="eyebrow">CATEGORY</td>
              <c:forEach items="${products}" var="p">
                <td>${p.category.label}</td>
              </c:forEach>
            </tr>
            <tr>
              <td class="eyebrow">PRICE</td>
              <c:forEach items="${products}" var="p">
                <td class="mono">&#8377;${p.finalPrice}</td>
              </c:forEach>
            </tr>
            <tr>
              <td class="eyebrow">STOCK</td>
              <c:forEach items="${products}" var="p">
                <td class="mono">${p.stockQuantity}</td>
              </c:forEach>
            </tr>
            <c:forEach items="${products[0].specifications}" var="entry">
              <tr>
                <td class="eyebrow">${fn:toUpperCase(entry.key)}</td>
                <c:forEach items="${products}" var="p">
                  <td>${p.specifications[entry.key]}</td>
                </c:forEach>
              </tr>
            </c:forEach>
            <tr>
              <td></td>
              <c:forEach items="${products}" var="p">
                <td>
                  <form method="post" action="${ctx}/kit/add">
                    <input type="hidden" name="productId" value="${p.id}" />
                    <input type="hidden" name="quantity" value="1" />
                    <input type="hidden" name="redirectTo" value="${ctx}/equipment/compare" />
                    <button type="submit" class="btn btn--primary btn--small" ${p.stockQuantity == 0 ? 'disabled' : ''}>+ KIT</button>
                  </form>
                </td>
              </c:forEach>
            </tr>
          </tbody>
        </table>
      </div>
    </c:if>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
