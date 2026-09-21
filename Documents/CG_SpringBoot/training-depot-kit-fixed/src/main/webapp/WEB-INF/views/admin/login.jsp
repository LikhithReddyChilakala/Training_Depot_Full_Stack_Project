<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Administrator Login" />
<%@ include file="/WEB-INF/views/fragments/head.jspf" %>
<%@ include file="/WEB-INF/views/fragments/header.jspf" %>

<main class="depot-shell page-shell section" data-grid-id="main">
  <div class="shell-full" style="max-width:420px; margin: 0 auto;">
    <span class="eyebrow">ADMIN CONTROL TERMINAL</span>
    <h1>Administrator Login</h1>
    <p style="margin: 0 0 var(--space-4);">Staff access only. Depot members should use the customer login.</p>

    <c:if test="${not empty error}"><div class="flash flash--error">${error}</div></c:if>

    <form method="post" action="${ctx}/admin/login" class="depot-panel-card">
      <input type="hidden" name="redirectTo" value="${redirectTo}" />
      <div class="depot-field">
        <label for="username">Administrator Username</label>
        <input type="text" id="username" name="username" required autofocus />
      </div>
      <div class="depot-field">
        <label for="password">Password</label>
        <input type="password" id="password" name="password" required />
      </div>
      <button type="submit" class="btn btn--primary btn--block">ENTER CONTROL TERMINAL</button>
    </form>

    <p style="margin-top: var(--space-4);">Not staff? <a href="${ctx}/login">Customer login</a>.</p>
  </div>
</main>

<%@ include file="/WEB-INF/views/fragments/footer.jspf" %>
