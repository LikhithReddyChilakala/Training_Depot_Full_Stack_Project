<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Depot Floor Issue &middot; Training Depot</title>
<link rel="stylesheet" href="${ctx}/css/tokens.css">
<link rel="stylesheet" href="${ctx}/css/base.css">
<link rel="stylesheet" href="${ctx}/css/components.css">
</head>
<body>
  <main class="page-shell section" style="text-align:center; padding-top: var(--space-8);">
    <span class="eyebrow">500 &middot; FLOOR ISSUE</span>
    <h1>Something Went Wrong On Our End</h1>
    <p style="margin: 0 auto var(--space-5); max-width:46ch;">
      The depot floor hit a snag handling that request. Nothing was charged or changed if you were mid-checkout. Try again in a moment.
    </p>
    <a href="${ctx}/" class="btn btn--primary">RETURN TO THE DEPOT</a>
  </main>
</body>
</html>
