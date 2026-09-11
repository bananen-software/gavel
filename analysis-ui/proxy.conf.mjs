/**
 * Dev server proxy.
 *
 * There is a collision worth understanding here. The backend's REST controllers
 * are mapped to /projects and /workspaces, and those are also Angular route
 * prefixes — /projects/1/packages/7 is a page. In dev both live on
 * localhost:4200, so a blanket proxy on /projects would forward page loads to
 * Spring, which would answer 404, and deep links plus hard refresh would break.
 *
 * The `bypass` hook separates them by intent rather than by path: a GET that
 * asks for HTML is a browser navigating, so it is handed back to the dev server
 * and renders the app; everything else is the app calling the API, so it is
 * proxied. Returning the request URL means "serve this normally"; returning
 * null means "proxy it".
 *
 * In production the collision does not exist, because the app is served under
 * /ui/ and its routes are /ui/projects/1. If you would rather not rely on this
 * at all, moving the controllers under @RequestMapping("/api/...") removes the
 * overlap outright, and is what I would do eventually.
 *
 * 127.0.0.1 rather than localhost: since Node 17, localhost resolves to ::1
 * first, while Tomcat commonly binds IPv4 only, which shows up as an
 * ECONNREFUSED in this terminal and a pending request in the browser.
 */

const backend = {
  target: 'http://127.0.0.1:8080',
  secure: false,
  changeOrigin: true,
};

/** @param {import('node:http').IncomingMessage} req */
const skipBrowserNavigations = (req) => {
  const accept = req.headers.accept ?? '';
  const isNavigation = req.method === 'GET' && accept.includes('text/html');
  return isNavigation ? req.url : null;
};

export default {
  '/graphql': backend,
  '^/(projects|workspaces)(/.*)?$': {
    ...backend,
    bypass: skipBrowserNavigations,
  },
};
