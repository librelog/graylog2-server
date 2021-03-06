import Version from 'util/Version';

class DocsHelper {
  PAGES = {
    ALERTS: 'streams/alerts.html',
    AUTHENTICATORS: 'users_and_roles/external_auth.html',
    CLUSTER_STATUS_EXPLAINED: 'configuration/elasticsearch.html#cluster-status-explained',
    COLLECTOR: 'collector.html',
    COLLECTOR_SIDECAR: 'sidecar.html',
    COLLECTOR_STATUS: 'sidecar.html#sidecar-status',
    CONFIGURING_ES: 'configuration/elasticsearch.html',
    DASHBOARDS: 'dashboards.html',
    DECORATORS: 'queries.html#decorators',
    ENTERPRISE_SETUP: 'enterprise/setup.html',
    ES_CLUSTER_STATUS_RED: 'configuration/elasticsearch.html#cluster-status-explained',
    ES_CLUSTER_UNAVAILABLE: 'configuration/elasticsearch.html#configuration',
    ES_OPEN_FILE_LIMITS: 'configuration/elasticsearch.html#open-file-limits',
    EXTRACTORS: 'extractors.html',
    INDEXER_FAILURES: 'indexer_failures.html',
    INDEX_MODEL: 'configuration/index_model.html',
    LOAD_BALANCERS: 'configuration/load_balancers.html',
    LOOKUPTABLES: 'lookuptables.html',
    PAGE_FLEXIBLE_DATE_CONVERTER: 'extractors.html#the-flexible-date-converter',
    PAGE_STANDARD_DATE_CONVERTER: 'extractors.html#the-standard-date-converter',
    PERMISSIONS: 'users_and_roles/permission_system.html',
    PIPELINE_FUNCTIONS: 'pipelines/functions.html',
    PIPELINE_RULES: 'pipelines/rules.html',
    PIPELINES: 'pipelines.html',
    REPORTING: 'reporting.html',
    ROLLING_ES_UPGRADE: 'upgrade/rolling_es_upgrade.html',
    SEARCH_QUERY_LANGUAGE: 'queries.html',
    STREAMS: 'streams.html',
    STREAM_PROCESSING_RUNTIME_LIMITS: 'streams.html#stream-processing-runtime-limits',
    UPGRADE_GUIDE: 'upgrade/graylog-%%version%%.html',
    USERS_ROLES: 'users_and_roles.html',
    WELCOME: '', // Welcome page to the documentation
  };

  DOCS_URL = 'https://docs.graylog.org/en/';

  toString(path) {
    const version = Version.getMajorAndMinorVersion();
    const baseUrl = this.DOCS_URL + version;

    return path === '' ? baseUrl : `${baseUrl}/pages/${path.replace('%%version%%', version)}`;
  }

  toLink(path, title) {
    return `<a href="${this.toString(path)}" target="_blank">${title}</a>`;
  }

  versionedDocsHomePage() {
    return this.toString('');
  }
}

const docsHelper = new DocsHelper();

export default docsHelper;
