<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="depot" tagdir="/WEB-INF/tags" %>
<c:set var="pageTitle" value="Training Depot" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section blueprint-guides" data-grid-id="main">
  <div class="shell-full">
    <span class="eyebrow">DEPOT NO. TD-001 &nbsp;&middot;&nbsp; CATALOG OF ${recordCount} RECORD<c:if test="${recordCount != 1}">S</c:if></span>
    <h1 style="margin-top: var(--space-2); font-family: var(--font-mono); text-transform: uppercase; letter-spacing: 0.02em;">Training Depot</h1>
    <p style="max-width: 46ch; color: var(--depot-graphite-dark);">An equipment archive and fitness supply floor. Look up a record, check what's on the shelf, and build a kit.</p>

    <form method="get" action="${ctx}/equipment" style="max-width: 420px; margin: var(--space-4) 0 var(--space-6);">
      <div class="depot-header__search" style="border-color: var(--depot-graphite-dark); background: var(--depot-panel-raised);">
        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>
        <input type="text" name="q" placeholder="LOOK FOR EQUIPMENT" />
      </div>
    </form>

    <div class="bin-row">
      <c:forEach items="${categories}" var="cat" varStatus="st">
        <a class="bin-pill" href="${ctx}/equipment?category=${cat}">BIN 0${st.index + 1} &middot; ${cat.label}</a>
      </c:forEach>
    </div>
  </div>

  <div class="shell-full" style="margin-top: var(--space-7);">
    <h2>Recently Racked</h2>
    <div class="record-grid">
      <c:forEach items="${preview}" var="product">
        <depot:equipmentRecordCard product="${product}" showCompare="${false}" />
      </c:forEach>
    </div>
    <p style="margin-top: var(--space-5);"><a href="${ctx}/equipment" class="btn btn--ghost">VIEW FULL EQUIPMENT INDEX</a></p>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
