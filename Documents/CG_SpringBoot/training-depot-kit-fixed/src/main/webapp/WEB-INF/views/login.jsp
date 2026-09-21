<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Depot Access" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full" style="max-width:420px; margin: 0 auto;">
    <span class="eyebrow">MEMBER ENTRY</span>
    <h1>Depot Access</h1>

    <c:if test="${not empty error}"><div class="flash flash--error">${error}</div></c:if>
    <c:if test="${not empty success}"><div class="flash flash--success">${success}</div></c:if>

    <form method="post" action="${ctx}/login" class="depot-panel-card">
      <input type="hidden" name="redirectTo" value="${redirectTo}" />
      <div class="depot-field">
        <label for="username">Username</label>
        <input type="text" id="username" name="username" required autofocus />
      </div>
      <div class="depot-field">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required />
      </div>
      <button type="submit" class="btn btn--primary btn--block">ENTER THE DEPOT</button>
    </form>

    <p style="margin-top: var(--space-4);">New here? <a href="${ctx}/register">Register for depot access</a>.</p>
    <p style="margin-top: var(--space-2);"><a href="${ctx}/admin/login" style="opacity:0.6; font-size:0.85em;">Administrator Login</a></p>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
