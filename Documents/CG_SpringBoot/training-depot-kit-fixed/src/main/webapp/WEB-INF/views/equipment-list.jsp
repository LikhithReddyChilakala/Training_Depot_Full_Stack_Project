<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Equipment Index" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <aside class="shell-rail">
    <h4 class="eyebrow" style="margin-bottom: var(--space-3);">FILTER BY BIN</h4>
    <ul style="display:flex; flex-direction:column; gap:6px;">
      <li>
        <a href="${ctx}/equipment<c:if test="${not empty query}">?q=${query}</c:if>"
           class="bin-pill" style="display:block; ${empty selectedCategory ? 'background:var(--depot-mark); color:#FBF3EA; border-color:var(--depot-mark-dim);' : ''}">
          ALL RECORDS
        </a>
      </li>
      <c:forEach items="${categories}" var="cat">
        <li>
          <a href="${ctx}/equipment?category=${cat}<c:if test="${not empty query}">&q=${query}</c:if>"
             class="bin-pill" style="display:block; ${selectedCategory == cat ? 'background:var(--depot-mark); color:#FBF3EA; border-color:var(--depot-mark-dim);' : ''}">
            ${cat.label}
          </a>
        </li>
      </c:forEach>
    </ul>
  </aside>

  <section class="shell-main">
    <div style="display:flex; justify-content:space-between; align-items:baseline; margin-bottom: var(--space-4);">
      <h1 style="margin:0;">
        <c:choose>
          <c:when test="${not empty selectedCategory}">${selectedCategory.label}</c:when>
          <c:otherwise>Equipment Index</c:otherwise>
        </c:choose>
      </h1>
      <span class="eyebrow">${fn:length(results)} RECORD<c:if test="${fn:length(results) != 1}">S</c:if></span>
    </div>

    <c:if test="${not empty query}">
      <p class="eyebrow">RESULTS FOR "${query}"</p>
    </c:if>

    <c:choose>
      <c:when test="${empty results}">
        <div class="empty-state">
          <p>No records match that search. Try a different term or clear the filter.</p>
          <a href="${ctx}/equipment" class="btn btn--ghost">CLEAR FILTER</a>
        </div>
      </c:when>
      <c:otherwise>
        <div class="record-grid">
          <c:forEach items="${results}" var="product">
            <depot:equipmentRecordCard product="${product}" />
          </c:forEach>
        </div>
      </c:otherwise>
    </c:choose>
  </section>
</main>

<div class="compare-bar">
  <span class="compare-bar__count">0 selected</span>
  <a class="btn btn--primary compare-bar__link" data-base-href="${ctx}/equipment/compare" href="#">COMPARE</a>
</div>

<script src="${ctx}/js/compare.js"></script>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
