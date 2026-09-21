<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Equipment Records" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full">
    <span class="eyebrow">INVENTORY CONTROL TERMINAL</span>
    <div style="display:flex; justify-content:space-between; align-items:baseline;">
      <h1>Equipment Records</h1>
      <a href="${ctx}/admin/products/new" class="btn btn--primary">+ NEW RECORD</a>
    </div>

    <c:if test="${not empty success}"><div class="flash flash--success">${success}</div></c:if>

    <div class="table-scroll">
      <table class="depot-table">
        <thead>
          <tr><th>Record</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th><th>Status</th><th></th></tr>
        </thead>
        <tbody>
          <c:forEach items="${products}" var="p">
            <tr>
              <td class="mono">${p.recordNumber}</td>
              <td>${p.name}</td>
              <td>${p.category.label}</td>
              <td class="mono">&#8377;${p.finalPrice}</td>
              <td style="min-width:160px;"><depot:stockMeter product="${p}" /></td>
              <td>
                <c:choose>
                  <c:when test="${p.active}"><span class="tag tag--confirmed">ACTIVE</span></c:when>
                  <c:otherwise><span class="tag tag--cancelled">INACTIVE</span></c:otherwise>
                </c:choose>
              </td>
              <td style="display:flex; gap:6px;">
                <a href="${ctx}/admin/products/${p.id}/edit" class="btn btn--ghost btn--small">EDIT</a>
                <form method="post" action="${ctx}/admin/products/${p.id}/delete" onsubmit="return confirm('Remove this record from the active floor?');">
                  <button type="submit" class="btn btn--danger btn--small">REMOVE</button>
                </form>
              </td>
            </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
