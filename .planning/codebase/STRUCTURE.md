# Codebase Structure

**Analysis Date:** 2026-04-03

## Directory Layout

```text
unicenta-opos/
├── .claude/                        # Claude Code project settings
├── .github/
│   ├── ISSUE_TEMPLATE/             # GitHub issue templates
│   └── workflows/
│       ├── ci.yml                  # Build + test + Codecov
│       ├── claude.yml              # Claude Code automation
│       └── semgrep.yml             # SAST security scanning
├── .planning/
│   └── codebase/                   # Architecture & codebase documentation
├── .vscode/                        # VS Code workspace settings
├── docs/
│   ├── reference/                  # Reference documentation
│   └── superpowers/                # Feature specs & plans
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/unicenta/       # Main application source (619 .java files)
│   │   │   ├── net/proteanit/sql/  # Vendored: SQL ResultSet utilities
│   │   │   └── org/usb4java/      # Vendored: USB device examples
│   │   └── resources/              # Config, i18n, images, reports, templates
│   ├── other/                      # Platform-specific native libs & bonus content
│   ├── scripts/                    # Startup scripts (start.sh, start.bat, etc.)
│   └── test/
│       └── java/com/unicenta/      # Unit tests (23 test classes)
├── pom.xml                         # Maven build configuration
├── CLAUDE.md                       # Project instructions for Claude Code
├── README.md                       # Project README
├── LICENSE                         # GPL v3
├── CONTRIBUTING.md                 # Contribution guidelines
├── CODE_OF_CONDUCT.md              # Code of conduct
├── SECURITY.md                     # Security policy
└── .coderabbit.yml                 # CodeRabbit review configuration
```

## Package Structure: `com.unicenta`

### Foundation Packages

**`com.unicenta.basic`** -- Core exception handling

- `BasicException.java` -- Base exception class for all application errors. Wraps cause exceptions with message strings. Used throughout all layers.

**`com.unicenta.beans`** -- Reusable Swing UI components

- `JNumberKeys.java` -- Numeric keypad widget (touch-friendly number input)
- `JNumberDialog.java` -- Modal dialog with numeric input
- `JNumberPop.java` -- Popup numeric input
- `JPasswordDialog.java` -- Password entry dialog
- `JCalendarDialog.java` / `JCalendarPanel.java` -- Date picker
- `JClockPanel.java` -- Clock display widget
- `JTimePanel.java` -- Time picker
- `JFlowPanel.java` -- Flow layout panel
- `JGuestsPop.java` -- Guest count popup (restaurant mode)
- `LocaleResources.java` -- Resource bundle loader for i18n
- `DateUtils.java` -- Date utility methods
- `RoundedBorder.java` -- Custom border renderer

**`com.unicenta.format`** -- Formatting and validation

- `Formats.java` -- Static formatting methods for STRING, INT, DOUBLE, CURRENCY, PERCENT, DATE, TIME, TIMESTAMP, HOURMIN, BYTEA, BOOLEAN. Configured at startup via `Formats.setCurrencyPattern()` etc.
- `FormatsValidate.java` -- Input validation rules
- `FormatsConstrain.java` -- Input constraints
- `FormatsException.java` -- Formatting error exception
- `DoubleUtils.java` -- Safe double arithmetic
- `FormatsRESOURCE.java` -- Resource-based formatting

**`com.unicenta.editor`** -- Generic data editor components

- `JEditorKeys.java` -- Editor keyboard panel
- `JEditorCurrency.java` / `JEditorCurrencyPositive.java` -- Currency input fields
- `JEditorDouble.java` / `JEditorDoublePositive.java` -- Decimal input fields
- `JEditorIntegerPositive.java` -- Integer input field
- `JEditorString.java` / `JEditorText.java` -- Text input fields
- `JEditorPassword.java` -- Password editor
- `JEditorNumber.java` -- Base numeric editor
- `EditorComponent.java` / `EditorKeys.java` -- Editor interfaces

### Data Access Layer: `com.unicenta.data`

**`com.unicenta.data.loader`** -- Database connectivity and SQL execution (83 files)

Core connection:

- `Session.java` -- JDBC connection wrapper. Holds `Connection`, manages transactions (`begin()`, `commit()`, `rollback()`). Auto-detects DB type via `getDiff()`.
- `SessionDB.java` (interface) -- Database-specific SQL dialect: `TRUE()`, `FALSE()`, `INTEGER_NULL()`, `CHAR_NULL()`, `getName()`, `getSequenceSentence()`
- `SessionDBMariaDB.java` -- MariaDB dialect
- `SessionDBMySQL.java` -- MySQL dialect
- `SessionDBPostgreSQL.java` -- PostgreSQL dialect
- `SessionDBDerby.java` -- Derby (embedded) dialect
- `SessionDBSQLite.java` -- SQLite dialect
- `SessionDBHSQLDB.java` -- HSQLDB dialect
- `SessionDBOracle.java` -- Oracle dialect
- `SessionDBGeneric.java` -- Fallback generic dialect

SQL execution:

- `BaseSentence.java` -- Abstract base for SQL operations
- `PreparedSentence.java` -- Parameterized SQL via `SerializerRead`/`SerializerWrite`
- `StaticSentence.java` -- Static SQL string execution
- `BatchSentence.java` -- Multiple SQL statements from text
- `BatchSentenceResource.java` -- Batch SQL from classpath resource files
- `BatchSentenceScript.java` -- Batch SQL from script
- `JDBCSentence.java` -- Low-level JDBC execution
- `Transaction.java` -- Transaction wrapper

Serialization (ResultSet <-> Object mapping):

- `SerializerRead.java` (interface) -- Maps `DataRead` to objects
- `SerializerWrite.java` (interface) -- Maps objects to `DataWrite`
- `SerializerReadString.java`, `SerializerReadInteger.java`, `SerializerReadDouble.java`, `SerializerReadDate.java`, `SerializerReadBytes.java`, `SerializerReadImage.java` -- Typed readers
- `SerializerWriteString.java`, `SerializerWriteInteger.java`, `SerializerWriteBasic.java`, `SerializerWriteBasicComposed.java` -- Typed writers
- `DataRead.java` (interface) -- Abstraction over ResultSet
- `DataWrite.java` (interface) -- Abstraction over PreparedStatement
- `Datas.java` -- Type enum: STRING, INT, DOUBLE, BOOLEAN, TIMESTAMP, BYTEA, IMAGE, etc.

Table metadata:

- `TableDefinition.java` -- Table name + column names + data types. Generates SELECT/INSERT/UPDATE/DELETE SQL.
- `DataField.java` -- Single column metadata

Sequence management:

- `SequenceForMySQL.java` -- MySQL sequence implementation
- `SequenceForDerby.java` -- Derby sequence implementation
- `SequenceForSQLite.java` -- SQLite sequence implementation

Query building:

- `QBFBuilder.java` -- Query-By-Form SQL builder
- `QBFCompareEnum.java` -- Comparison operators for QBF
- `NormalBuilder.java` -- Standard SQL builder
- `SimpleBuilder.java` -- Simple SQL builder

Utility:

- `ImageUtils.java` -- Image read/write from BLOB columns
- `Vectorer.java` / `VectorerBasic.java` -- Object-to-string-array conversion for table display
- `ComparatorCreator.java` -- Sort comparator factory
- `KeyGetterBasic.java` / `KeyGetterFirst.java` -- Primary key extraction
- `TicketHeader.java` / `TicketFooter.java` -- Receipt header/footer from config

**`com.unicenta.data.model`** -- Schema metadata classes

- `Table.java` -- Represents a database table
- `Row.java` -- Represents a row with fields
- `Column.java` -- Represents a column definition
- `Field.java` -- Field with data type
- `PrimaryKey.java` -- Primary key definition

**`com.unicenta.data.gui`** -- Data-related UI components

- `JMessageDialog.java` -- Modal message/error dialog with severity icons
- `MessageInf.java` -- Message object with severity levels (SGN_DANGER, SGN_WARNING, SGN_NOTICE, SGN_SUCCESS, SGN_IMPORTANT)
- `ComboBoxValModel.java` -- ComboBox model for value objects
- `ListKeyed.java` -- Keyed list model
- `JImageEditor.java` -- Image upload/edit component
- `JImageViewerCustomer.java` / `JImageViewerProduct.java` -- Image display
- `JNavigator.java` -- Record navigation (first/prev/next/last)
- `JListNavigator.java` -- List-based navigation
- `JCounter.java` -- Record counter display
- `JFind.java` -- Search dialog
- `JSaver.java` -- Save/discard/delete actions panel
- `JSort.java` -- Sort configuration dialog
- `CompoundIcon.java` -- Composite icon renderer
- `NullIcon.java` -- Empty icon placeholder

**`com.unicenta.data.user`** -- Browsable data framework

- `BrowsableEditableData.java` -- Main CRUD controller: manages state (ST_NORECORD, ST_UPDATE, ST_DELETE, ST_INSERT), handles navigation, save, delete operations
- `BrowsableData.java` -- Read-only browsable data
- `BrowseListener.java` / `StateListener.java` -- State change listeners
- `DirtyManager.java` / `DirtyListener.java` -- Change tracking (marks unsaved edits)
- `EditorRecord.java` -- Interface for edit forms: `writeValueInsert()`, `writeValueEdit()`, `writeValueDelete()`
- `EditorCreator.java` -- Factory for editor instances
- `ListProvider.java` / `ListProviderCreator.java` -- Data list providers
- `SaveProvider.java` -- Save/update/delete operations
- `Finder.java` -- Search/filter interface
- `DocumentLoader.java` -- Document loading interface

### POS Application: `com.unicenta.pos`

**`com.unicenta.pos.forms`** -- Application framework (42 files)

Entry points and frames:

- `StartPOS.java` -- `main()` method. Config load, L&F setup, frame creation.
- `JRootFrame.java` -- Windowed JFrame. Creates JRootApp, registers RMI instance.
- `JRootKiosk.java` -- Fullscreen JFrame variant.
- `JRootApp.java` (1216 lines) -- Main application JPanel. DB init, schema migration, device init, user login, cash register state.

Configuration:

- `AppConfig.java` (399 lines) -- Properties file reader/writer. Loads from `~/{APP_ID}.properties`. Default values for DB, printer, locale, payment, device, table settings. Singleton via `getInstance()`.
- `AppProperties.java` (interface) -- Property access contract: `getProperty()`, `getHost()`, `getConfigFile()`
- `AppLocal.java` -- Static i18n access: `getIntString(key)`. Loads `pos_messages` and `erp_messages` bundles.

Interfaces:

- `AppView.java` (interface) -- Application context: `getSession()`, `getDeviceTicket()`, `getDeviceScale()`, `getDeviceScanner()`, `getBean()`, `getActiveCashIndex()`, etc.
- `AppUserView.java` (interface) -- User-specific view: `showTask()`, `getUser()`, `getAppView()`
- `JPanelView.java` (interface) -- Switchable panel contract: `getTitle()`, `activate()`, `deactivate()`, `getComponent()`

BeanFactory hierarchy:

- `BeanFactory.java` (interface) -- `getBean(): Object`
- `BeanFactoryApp.java` (interface) -- extends BeanFactory, adds `init(AppView)`
- `BeanFactoryDataSingle.java` (abstract) -- Base for DataLogic classes. `init(Session)` abstract method.
- `BeanFactoryCache.java` -- Caching decorator
- `BeanFactoryData.java` -- Multi-bean factory
- `BeanFactoryObj.java` -- Wraps arbitrary object
- `BeanFactoryScript.java` -- Script-based factory
- `BeanFactoryException.java` -- Factory error

DataLogic services (in forms package):

- `DataLogicSales.java` (2827 lines) -- Products, categories, tickets, ticket lines, payments, taxes, stock diary, suppliers, vouchers. The largest and most central DataLogic class.
- `DataLogicSystem.java` (891 lines) -- Resources, people/users, permissions, cash register, orders, pickup IDs, places.
- `DataLogicOrders.java` -- Kitchen order management.

User management:

- `AppUser.java` -- User model: id, name, card, password, role, icon, permissions (Set<String>). Parses role XML for permissions.
- `AppViewConnection.java` -- JDBC connection factory. Handles multi-DB selection, password decryption, driver loading.

Menu system:

- `JPrincipalApp.java` (649 lines) -- Per-user application view. Script-driven menu via BeanShell. Lazy-loads panels via BeanFactory.
- `MenuDefinition.java` / `MenuElement.java` -- Menu structure model
- `MenuPanelAction.java` -- Action that opens a panel by class name
- `MenuExecAction.java` -- Action that executes a process by class name
- `MenuItemDefinition.java` / `MenuTitleDefinition.java` -- Menu item types
- `JPanelMenu.java` -- Menu panel UI
- `JPanelNull.java` -- Empty placeholder panel

Other:

- `Payments.java` -- Payment constants/types
- `ProcessAction.java` -- Generic process execution action
- `DriverWrapper.java` -- JDBC driver wrapper for classloader isolation
- `JDlgChangePassword.java` -- Password change dialog

**`com.unicenta.pos.sales`** -- Sales workflow (52 files across sub-packages)

Core sales:

- `JPanelTicket.java` (3515 lines) -- The main sales screen. Manages current ticket, product selection, line editing, payment flow, printing. The largest class in the codebase.
- `JPanelTicketSales.java` -- Extends JPanelTicket for standard retail sales. Adds JCatalog product browser.
- `JPanelTicketEdits.java` -- Extends JPanelTicket for ticket editing/refunds.
- `JTicketsBag.java` -- Abstract base for ticket bag (manages open tickets). Factory method `createTicketsBag()` returns mode-specific implementation.
- `JTicketLines.java` -- Ticket line items list display
- `JTicketCatalogLines.java` -- Catalog-aware line display
- `TaxesLogic.java` -- Tax calculation engine. Applies tax rules from `TaxesLogicElement` list.
- `DataLogicReceipts.java` -- Receipt persistence (shared tickets in DB)
- `SharedTicketInfo.java` -- Shared/parked ticket info
- `ReprintTicketInfo.java` -- Reprint data
- `SimpleReceipt.java` -- Simple receipt display
- `ReceiptSplit.java` -- Receipt splitting dialog
- `JProductLineEdit.java` -- Edit individual ticket line
- `JRefundLines.java` -- Select lines for refund
- `KitchenDisplay.java` -- Kitchen order display (JavaFX)
- `JSalesLayoutManager.java` -- Sales screen layout manager
- `MenuActionListener.java` -- Sales menu action handler

Sub-packages:

- `sales/restaurant/` -- Restaurant mode: `JTicketsBagRestaurant.java`, `JTicketsBagRestaurantMap.java`, `RestaurantDBUtils.java`, `Floor.java`, `Place.java`, `JCalendarItemRenderer.java`
- `sales/shared/` -- Shared ticket mode: `JTicketsBagShared.java`, `JTicketsBagSharedList.java`, `JTicketsReprintList.java`
- `sales/simple/` -- Simple mode: `JTicketsBagSimple.java`

**`com.unicenta.pos.ticket`** -- Transaction domain models (38 files)

- `TicketInfo.java` (772 lines) -- Main receipt/transaction model. Holds line items, payments, taxes, customer reference, user, date. Implements `SerializableRead`, `Externalizable`.
- `TicketLineInfo.java` -- Individual line item: product reference, quantity, price, tax, attributes
- `TicketTaxInfo.java` -- Tax breakdown per tax category
- `ProductInfo.java` -- Basic product data
- `ProductInfoExt.java` -- Extended product with category, tax, image, attributes
- `ProductInfoEdit.java` -- Editable product variant
- `CategoryInfo.java` -- Product category
- `TaxInfo.java` -- Tax rate definition
- `UserInfo.java` -- User reference (id + name)
- `CardInfo.java` -- Payment card info
- `FindTicketsInfo.java` -- Ticket search results
- `HostInfo.java` -- Host/machine info
- `SalesDetailInfo.java` -- Sales detail aggregation
- `ProviderInfo.java` -- Provider reference
- Various filter classes: `CategoryFilter.java`, `ProductFilter.java`, `ProductFilterSales.java`, `LocationFilterSales.java`
- Renderer/vectorer: `ProductRenderer.java`, `ProductVectorer.java`, `FindTicketsRenderer.java`

**`com.unicenta.pos.payment`** -- Payment processing (57 files)

Domain models:

- `PaymentInfo.java` (abstract) -- Base: `getName()`, `getTotal()`, `getPaid()`, `getChange()`, `getTendered()`, `copyPayment()`, `getTransactionID()`, `getCardName()`, `getVoucher()`
- `PaymentInfoCash.java` -- Cash payment with change calculation
- `PaymentInfoFree.java` -- Zero-amount payment
- `PaymentInfoMagcard.java` -- Credit/debit card payment
- `PaymentInfoMagcardRefund.java` -- Card refund
- `PaymentInfoTicket.java` -- Debt/on-account payment
- `VoucherPaymentInfo.java` -- Voucher/prepaid payment
- `PaymentInfoList.java` -- Collection of payments for a transaction
- `PaymentInfoCash_original.java` -- Legacy backup (unused)

UI panels:

- `JPaymentSelect.java` -- Payment method selection dialog
- `JPaymentSelectReceipt.java` -- Receipt payment flow
- `JPaymentSelectRefund.java` -- Refund payment flow
- `JPaymentSelectCustomer.java` -- Customer-specific payment
- `JPaymentCashPos.java` -- Cash payment panel with quick-amount buttons
- `JPaymentFree.java` -- Free payment panel
- `JPaymentCheque.java` -- Cheque payment panel
- `JPaymentDebt.java` -- Debt/credit payment panel
- `JPaymentBank.java` -- Bank transfer panel
- `JPaymentSlip.java` -- Slip payment panel
- `JPaymentVoucher.java` -- Voucher payment panel
- `JPaymentMagcard.java` -- Card payment panel
- `JPaymentRefund.java` -- Refund panel
- `JPaymentPaper.java` -- Paper-based payment panel

Payment gateway:

- `PaymentGateway.java` (interface) -- External payment processing
- `PaymentGatewayFac.java` -- Gateway factory
- `PaymentGatewayExt.java` -- Extended gateway interface
- `PaymentGatewayPaymentSense.java` -- PaymentSense integration
- `PaymentConfiguration.java` -- Gateway configuration
- `PaymentPanel.java` / `PaymentPanelBasic.java` / `PaymentPanelType.java` -- Gateway UI
- `PaymentPanelFac.java` / `PaymentPanelEMV.java` / `PaymentPanelMagCard.java` -- Specific panels

Card readers:

- `MagCardReader.java` (interface) -- Card reader abstraction
- `MagCardReaderFac.java` -- Card reader factory
- `MagCardReaderGeneric.java` -- Generic reader
- `MagCardReaderIntelligent.java` -- Smart reader

**`com.unicenta.pos.customers`** -- Customer management (18 files)

- `CustomerInfo.java` -- Basic customer data
- `CustomerInfoExt.java` -- Extended customer with all fields
- `CustomerInfoGlobal.java` -- Global customer state holder
- `DataLogicCustomers.java` (380 lines) -- Customer CRUD via TableDefinition
- `CustomersPanel.java` -- Customer list/CRUD panel (extends JPanelTable)
- `CustomersView.java` -- Customer edit form
- `CustomersPayment.java` -- Customer payment management
- `JCustomerFinder.java` -- Customer search dialog
- `JDialogNewCustomer.java` -- New customer dialog
- `OrderCustomerList.java` -- Customer order history
- `CustomerRenderer.java` -- List cell renderer
- `CustomerTransaction.java` -- Customer transaction record
- `TicketSelector.java` -- Ticket selection for customer

**`com.unicenta.pos.inventory`** -- Stock and product management (74 files)

Product management:

- `ProductsPanel.java` -- Product list/CRUD (extends JPanelTable)
- `ProductsEditor.java` -- Product edit form
- `ProductsWarehousePanel.java` / `ProductsWarehouseEditor.java` -- Per-warehouse stock
- `CategoriesPanel.java` / `CategoriesEditor.java` -- Category management
- `BundlePanel.java` / `BundleEditor.java` -- Product bundles
- `AuxiliarPanel.java` / `AuxiliarEditor.java` -- Auxiliary products

Stock operations:

- `StockManagement.java` -- Stock diary management
- `StockDiaryPanel.java` / `StockDiaryEditor.java` -- Stock diary entry
- `JInventoryLines.java` -- Inventory line items
- `InventoryRecord.java` / `InventoryLine.java` -- Inventory records
- `MovementReason.java` -- Stock movement reasons (IN, OUT, transfer, etc.)
- `StockModel.java` -- Stock data model
- `ProductStock.java` -- Product stock level
- `PriceImportPanel.java` -- Price import UI

Tax management:

- `TaxPanel.java` / `TaxEditor.java` -- Tax rule management
- `TaxCategoriesPanel.java` / `TaxCategoriesEditor.java` -- Tax categories
- `TaxCustCategoriesPanel.java` / `TaxCustCategoriesEditor.java` -- Customer tax categories
- `TaxCategoryInfo.java` / `TaxCustCategoryInfo.java` -- Tax category info

Attributes:

- `AttributesPanel.java` / `AttributesEditor.java` -- Product attributes
- `AttributeValuesPanel.java` / `AttributeValuesEditor.java` -- Attribute values
- `AttributeSetsPanel.java` / `AttributeSetsEditor.java` -- Attribute sets
- `AttributeUsePanel.java` / `AttributeUseEditor.java` -- Attribute usage
- `AttributeInfo.java` / `AttributeSetInfo.java` -- Info objects

Other:

- `LocationsPanel.java` / `LocationsView.java` / `LocationInfo.java` -- Warehouse locations
- `UomPanel.java` / `UomEditor.java` / `UomInfo.java` -- Units of measure
- `ProductsBundleInfo.java` / `MaterialProdInfo.java` -- Bundle/material info
- `JDlgUploadProducts.java` -- Bulk product upload
- `CodeType.java` -- Barcode type enum

**`com.unicenta.pos.catalog`** -- Product catalog UI (8 files)

- `JCatalog.java` -- Product catalog browser with category tabs and product grid
- `JCatalogTab.java` -- Single category tab
- `JProductsSelector.java` -- Product selection panel
- `CatalogSelector.java` (interface) -- Catalog interface: `loadCatalog()`, `showCatalogPanel()`
- `CategoryStock.java` -- Category with stock info

**`com.unicenta.pos.panels`** -- Shared UI panels (30 files)

- `JPanelTable.java` -- Abstract CRUD panel base. Integrates BrowsableEditableData, DirtyManager, ListProvider.
- `JPanelTable2.java` -- Variant CRUD panel
- `JPanelCloseMoney.java` -- Cash register close/reconciliation
- `JPanelCloseMoneyReprint.java` -- Reprint close report
- `JPanelPayments.java` -- Payment history browser
- `JPanelPrinter.java` -- Printer test/config panel
- `JProductFinder.java` -- Product search dialog
- `JTicketsFinder.java` -- Ticket search dialog
- `PaymentsModel.java` / `PaymentsReprintModel.java` -- Payment data models
- `PaymentsEditor.java` -- Payment display editor
- `ComboItemLocal.java` -- Localized combo box item
- `JTextFieldLimit.java` -- Text field with character limit
- `SQLDatabase.java` / `SQLTable.java` / `SQLColumn.java` / `SQLTableModel.java` -- SQL schema browser

**`com.unicenta.pos.reports`** -- Reporting framework (25 files)

- `JPanelReport.java` -- Abstract report panel base. Loads .jrxml, compiles JasperReport, fills with JDBC Connection.
- `PanelReportBean.java` -- BeanShell-configured report panel (loaded from `.bs` files)
- `JRDataSourceBasic.java` -- Custom JasperReports data source
- `ReportEditorCreator.java` -- Report parameter editor factory
- `ReportFields.java` / `ReportFieldsArray.java` -- Report field definitions
- Parameter panels: `JParamsDatesInterval.java`, `JParamsLocation.java`, `JParamsCustomer.java`, `JParamsUser.java`, `JParamsText.java`, `JParamsReason.java`, `JParamsSuppliers.java`, `JParamsComposed.java`

**`com.unicenta.pos.printer`** -- Printing abstraction (47 files across sub-packages)

Core:

- `DeviceTicket.java` -- Manages up to 6 printer outputs. Factory for printer instances based on config.
- `TicketParser.java` -- Parses XML ticket templates. Executes embedded BeanShell/Velocity for dynamic content.
- `DevicePrinter.java` (interface) -- Printer abstraction: `beginReceipt()`, `printLine()`, `endReceipt()`
- `DevicePrinterNull.java` -- Null object (no-op)
- `DeviceDisplay.java` (interface) -- Customer display abstraction
- `DeviceDisplayNull.java` / `DeviceDisplayBase.java` / `DeviceDisplayImpl.java` -- Display implementations
- `DeviceFiscalPrinter.java` / `DeviceFiscalPrinterNull.java` -- Fiscal printer support

ESC/POS thermal printers (`printer/escpos/`):

- `DevicePrinterESCPOS.java` -- ESC/POS printer implementation
- `ESCPOS.java` -- ESC/POS command constants
- `Codes*.java` -- Printer-specific command sets (Epson, Ithaca, Star, SurePOS, TMU220)
- `PrinterWritter*.java` -- Output writers (serial, file, raw)
- `UnicodeTranslator*.java` -- Character encoding translators

JavaPOS (`printer/javapos/`):

- `DevicePrinterJavaPOS.java` -- JavaPOS standard printer
- `DeviceDisplayJavaPOS.java` -- JavaPOS display
- `DeviceFiscalPrinterJavaPOS.java` -- JavaPOS fiscal printer

Screen display (`printer/screen/`):

- `DevicePrinterPanel.java` -- On-screen ticket preview
- `DeviceDisplayPanel.java` / `DeviceDisplayWindow.java` -- On-screen customer display

Ticket formatting (`printer/ticket/`):

- `BasicTicket.java` -- Ticket data model
- `BasicTicketForPrinter.java` / `BasicTicketForScreen.java` -- Format adapters
- `PrintItem*.java` -- Print elements: line, image, barcode

Animations:

- `DisplayAnimator.java` -- Base animation
- `ScrollAnimator.java`, `FlyerAnimator.java`, `CurtainAnimator.java`, `BlinkAnimator.java`, `NullAnimator.java` -- Display animations

**`com.unicenta.pos.admin`** -- Administration (17 files)

- `DataLogicAdmin.java` -- Admin CRUD: people, roles, resources
- `PeoplePanel.java` / `PeopleView.java` -- User management
- `RolesPanel.java` / `RolesView.java` -- Role management
- `ResourcesPanel.java` / `ResourcesView.java` -- Database resource management (templates, images, scripts stored in DB)
- `PeopleInfo.java` / `RoleInfo.java` / `RoleExtInfo.java` -- Info objects
- `JPeopleFinder.java` -- People search dialog
- `OWWatch.java` -- OneWire iButton watcher
- `ResourceType.java` -- Resource type enum

**`com.unicenta.pos.config`** -- Configuration UI (27 files)

- `JFrmConfig.java` -- Main configuration dialog (shown when DB fails or from menu)
- `JPanelConfiguration.java` -- Configuration panel container
- `JPanelConfigDatabase.java` -- Database connection settings
- `JPanelConfigGeneral.java` -- General settings
- `JPanelConfigLocale.java` -- Language/locale settings
- `JPanelConfigPeripheral.java` -- Peripheral device settings
- `JPanelConfigPayment.java` -- Payment gateway settings
- `JPanelConfigCompany.java` -- Company information
- `JPanelConfigSystem.java` -- System settings
- `JPanelConfigERP.java` -- ERP integration settings
- `JPanelTicketSetup.java` -- Ticket header/footer customization
- `PanelConfig.java` (interface) -- Config panel contract
- `ParametersConfig.java` / `ParametersPrinter.java` -- Parameter editors

**`com.unicenta.pos.suppliers`** -- Supplier management (16 files)

- `DataLogicSuppliers.java` -- Supplier CRUD
- `SuppliersPanel.java` / `SuppliersView.java` -- Supplier list/edit
- `JSupplierFinder.java` / `JDialogNewSupplier.java` -- Supplier search/create
- `SupplierInfo.java` / `SupplierInfoExt.java` / `SupplierInfoGlobal.java` -- Info objects
- `OrderSupplierList.java` -- Supplier order history
- `SupplierRenderer.java` / `SupplierTicketSelector.java` -- UI helpers
- `SupplierTransaction.java` -- Supplier transaction record

**`com.unicenta.pos.epm`** -- Employee Presence Management (17 files)

- `DataLogicPresenceManagement.java` -- Employee presence CRUD
- `JPanelEmployeePresence.java` -- Check-in/check-out panel
- `BreaksPanel.java` / `BreaksView.java` / `Break.java` / `BreaksInfo.java` -- Break management
- `LeavesPanel.java` / `LeavesView.java` / `LeavesInfo.java` -- Leave management
- `EmployeeInfo.java` / `EmployeeInfoExt.java` -- Employee data
- `JEmployeeFinder.java` -- Employee search

**`com.unicenta.pos.voucher`** -- Voucher/prepaid system (6 files)

- `VoucherPanel.java` / `VoucherEditor.java` -- Voucher management
- `VoucherInfo.java` -- Voucher data
- `JDialogReportPanel.java` -- Voucher report dialog

**`com.unicenta.pos.imports`** -- Data import (10 files)

- `JPanelCSVImport.java` -- CSV product import
- `CustomerCSVImport.java` -- CSV customer import
- `StockQtyImport.java` -- Stock quantity import
- `JPanelCSVCleardb.java` -- Clear database (dangerous)
- `JPanelCSV.java` -- CSV import base panel

**`com.unicenta.pos.mant`** -- Maintenance: floors and tables (10 files)

- `JPanelFloors.java` / `FloorsEditor.java` / `FloorsInfo.java` / `Floors.java` -- Floor plan management
- `JPanelPlaces.java` / `PlacesEditor.java` / `Places.java` -- Table/place management
- `Applications.java` -- Application info maintenance

**`com.unicenta.pos.util`** -- Utility classes (38 files)

- `Hashcypher.java` -- SHA-1/MD5 password hashing
- `AltEncrypter.java` -- Symmetric encryption for stored passwords
- `Base64Encoder.java` -- Base64 encoding
- `StringUtils.java` -- String utilities (resource reading, etc.)
- `StringParser.java` -- Simple string tokenizer
- `RoundUtils.java` -- Rounding utilities
- `LuhnAlgorithm.java` -- Luhn credit card validation
- `OSValidator.java` -- OS detection (Windows/Mac/Linux)
- `ThumbNailBuilder.java` -- Image thumbnail generation
- `BarcodeImage.java` -- Barcode image generation
- `InactivityListener.java` -- User inactivity timer
- `SessionKeepAlive.java` -- DB connection keep-alive
- `JRPrinterAWT300.java` -- JasperReports AWT printer
- `JRViewer400.java` -- JasperReports viewer panel
- `ReportUtils.java` -- Report utility methods
- `SelectPrinter.java` -- Printer selection dialog
- `FlatLookAndFeel.java` -- L&F utility
- `SwingUtils.java` -- Swing helper methods
- `PropertyUtils.java` -- Property file utilities
- `FtpUpload.java` -- FTP file upload
- Various HTML testers, video player, FX web browser

**`com.unicenta.pos.instance`** -- Single-instance enforcement (3 files)

- `InstanceManager.java` -- RMI server registration
- `InstanceQuery.java` -- RMI client query
- `AppMessage.java` (interface) -- `restoreWindow()` via RMI

**`com.unicenta.pos.scripting`** -- Script engine abstraction (5 files)

- `ScriptFactory.java` -- Factory: `BEANSHELL` and `VELOCITY` engine types
- `ScriptEngine.java` (interface) -- `put()`, `eval()` methods
- `ScriptEngineBeanshell.java` -- BeanShell wrapper
- `ScriptEngineVelocity.java` -- Velocity template wrapper
- `ScriptException.java` -- Script error

**Other small packages:**

- `com.unicenta.pos.scale/` (12 files) -- Weight scale device drivers
- `com.unicenta.pos.scanpal2/` (5 files) -- Barcode scanner integration
- `com.unicenta.pos.comm/` (1 file) -- `CommStream.java` serial communication
- `com.unicenta.pos.transfer/` (4 files) -- Database transfer between instances
- `com.unicenta.pos.resets/` (3 files) -- System reset operations
- `com.unicenta.orderpop/` (2 files) -- JavaFX order popup display

### Vendored Third-Party Code

- `net.proteanit.sql.DbUtils` -- ResultSet-to-TableModel adapter for Swing JTable
- `org.usb4java.examples.ListDevices` -- USB device listing example

## Resource Files

**Localization bundles** (`src/main/resources/`):

- `pos_messages*.properties` -- POS UI messages (14+ locales)
- `data_messages*.properties` -- Data layer messages
- `erp_messages*.properties` -- ERP integration messages
- `beans_messages*.properties` -- Bean component messages
- Per-report bundles in `src/main/resources/com/unicenta/reports/` (e.g., `sales_messages_nl.properties`)

**SQL scripts** (`src/main/resources/com/unicenta/pos/scripts/`):

- `MariaDB-create.sql` / `MySQL-create.sql` / `PostgreSQL-create.sql` / `Derby-create.sql` / `SQLite-create.sql` -- Schema creation per engine
- `MariaDB-upgrade_master.sql` / `MySQL-upgrade_master.sql` -- Schema migration scripts
- `MySQL-FKeys.sql` / `MySQL-dropFKeys.sql` -- Foreign key management
- `MySQL-clearData.sql` -- Data cleanup
- `MySQL-create-sp.sql` -- Stored procedures
- Various `MySQL-upgrade-4.5.*.sql` -- Version-specific upgrades

**Templates** (`src/main/resources/com/unicenta/pos/templates/`):

- `Menu.Root.txt` -- BeanShell menu definition script
- `Printer.Ticket.xml` / `Printer.Ticket2.xml` -- Receipt templates (XML with embedded BeanShell)
- `Printer.CloseCash.xml` -- Cash close receipt template
- `Ticket.Buttons.xml` -- Sales button configuration
- `Ticket.Line.xml` / `Ticket.Close.xml` -- Ticket workflow scripts
- `Role.Administrator.xml` / `Role.Employee.xml` / `Role.Manager.xml` / `Role.Guest.xml` -- Permission definitions
- `Window.Title.txt` -- Window title template

**Reports** (`src/main/resources/com/unicenta/reports/`):

- `.bs` files -- BeanShell report configuration (data source, parameters, report file)
- `.jrxml` files -- JasperReports XML report designs
- Categories: customers, sales, inventory, products, suppliers, users, epm, tools, vouchers, labels

**Images** (`src/main/resources/com/unicenta/images/`):

- 150+ PNG icons for buttons, menu items, products, currency denominations
- `favicon.png` -- Application icon
- `uniCenta_splash_dark.png` -- Splash screen

**JavaFX** (`src/main/resources/fxml/` and `src/main/resources/styles/`):

- `OrderPop.fxml` -- Order popup layout
- `orderpop.css` -- Order popup styling

## Naming Conventions

**Files:**

- Java classes: `PascalCase.java` (matches class name exactly)
- NetBeans forms: `PascalCase.form` (paired with `.java`)
- Properties: `snake_case.properties` (e.g., `pos_messages_nl.properties`)
- SQL scripts: `Engine-action.sql` (e.g., `MariaDB-create.sql`)
- Report configs: `domain_reportname.bs` (e.g., `sales_closedpos.bs`)
- Report designs: `domain_reportname.jrxml` (matches `.bs` filename)
- Templates: `Category.Purpose.xml` or `Category.Purpose.txt`

**Classes (naming patterns to follow):**

- Domain models: `*Info` suffix (`TicketInfo`, `PaymentInfoCash`, `CategoryInfo`, `CustomerInfoExt`)
- Extended models: `*InfoExt` suffix (`ProductInfoExt`, `CustomerInfoExt`, `SupplierInfoExt`)
- Global state: `*InfoGlobal` suffix (`CustomerInfoGlobal`, `SupplierInfoGlobal`)
- DataLogic services: `DataLogic*` prefix (`DataLogicSales`, `DataLogicCustomers`, `DataLogicAdmin`)
- CRUD panels: `*Panel` suffix extending `JPanelTable` (`CustomersPanel`, `ProductsPanel`)
- Edit forms: `*View` or `*Editor` suffix (`CustomersView`, `ProductsEditor`, `CategoriesEditor`)
- Search dialogs: `J*Finder` (`JCustomerFinder`, `JProductFinder`, `JTicketsFinder`)
- Create dialogs: `JDialogNew*` (`JDialogNewCustomer`, `JDialogNewSupplier`)
- Swing components: `J` prefix (Swing convention: `JRootApp`, `JPanelTicket`, `JCatalog`)
- Renderers: `*Renderer` (`CustomerRenderer`, `ProductRenderer`, `PeopleRenderer`)
- Device drivers: `Device*` (`DevicePrinter`, `DeviceScale`, `DeviceScanner`)
- Abstract panels: `JPanel*` (`JPanelTable`, `JPanelReport`, `JPanelView`)

**Variables (Hungarian notation, legacy convention):**

- `m_s` prefix: String fields (`m_sId`, `m_sName`, `m_sHost`)
- `m_d` prefix: Date/Double fields (`m_dDate`, `m_dActiveCashDateStart`)
- `m_i` prefix: int fields (`m_iTicketId`, `m_iActiveCashSequence`)
- `m_a` prefix: array/list fields (`m_aLines`, `m_aBeanFactories`)
- `m_dl` prefix: DataLogic references (`m_dlSystem`, `m_dlSales`)
- `m_j` prefix: Swing component references (`m_jPanelLeft`, `m_jLblTitle`)

## Where to Add New Code

**New business feature (e.g., loyalty program):**

1. Domain model: Create `LoyaltyInfo.java` in `src/main/java/com/unicenta/pos/loyalty/`
2. DataLogic: Create `DataLogicLoyalty.java` extending `BeanFactoryDataSingle` in same package or in `forms/`
3. CRUD panel: Create `LoyaltyPanel.java` extending `JPanelTable` in `src/main/java/com/unicenta/pos/loyalty/`
4. Edit form: Create `LoyaltyEditor.java` implementing `EditorRecord` in same package
5. Menu entry: Add to `src/main/resources/com/unicenta/pos/templates/Menu.Root.txt`
6. Permissions: Add to `Role.*.xml` templates
7. DB schema: Add CREATE TABLE to `MariaDB-create.sql`, `MySQL-create.sql`, etc.
8. Tests: Create `LoyaltyInfoTest.java` in `src/test/java/com/unicenta/pos/loyalty/`

**New report:**

1. Create `myreport.jrxml` in `src/main/resources/com/unicenta/reports/`
2. Create `myreport.bs` (BeanShell config) in same directory
3. Add menu entry in `Menu.Root.txt`: `submenu.addPanel("icon", "Menu.MyReport", "/com/unicenta/reports/myreport.bs")`
4. Add i18n keys to `*_messages.properties` files

**New payment method:**

1. Create `PaymentInfoXxx.java` extending `PaymentInfo` in `src/main/java/com/unicenta/pos/payment/`
2. Create `JPaymentXxx.java` extending JPanel + implementing `JPaymentInterface` in same package
3. Register in `JPaymentSelectReceipt.java` payment type list
4. Tests: Create `PaymentInfoXxxTest.java` in `src/test/java/com/unicenta/pos/payment/`

**New device driver (printer/scale/scanner):**

1. Create implementation in appropriate package (`printer/`, `scale/`, `scanpal2/`)
2. Register in factory: `DeviceTicket` (printers), `DeviceScale` (scales), `DeviceScannerFactory` (scanners)
3. Add config option in `JPanelConfigPeripheral.java`

**New utility function:**

- Place in `src/main/java/com/unicenta/pos/util/` for POS-specific utilities
- Place in `src/main/java/com/unicenta/format/` for formatting functions
- Place in `src/main/java/com/unicenta/data/loader/` for data access helpers

**Internationalization:**

- Add keys to `src/main/resources/pos_messages.properties` (default English)
- Add translations to locale-specific files (e.g., `pos_messages_nl.properties`)
- Access via `AppLocal.getIntString("key")`

## Special Directories

**`target/`:**
- Purpose: Maven build output
- Generated: Yes
- Committed: No (in `.gitignore`)
- Contains: `unicentaopos.jar`, `lib/` (dependencies), `classes/`, `reports/`, `locales/`

**`src/other/`:**
- Purpose: Platform-specific native libraries, bonus content, config templates
- Generated: No
- Committed: Yes
- Contains: Windows/Linux/Mac native libs (RXTX serial), bonus images, sample configs, license files

**`src/scripts/`:**
- Purpose: Application startup scripts (NOT database SQL scripts)
- Generated: No
- Committed: Yes
- Contains: `start.sh`, `start.bat`, `configure.sh`, `configure.bat`

**`.planning/codebase/`:**
- Purpose: Codebase analysis documents for GSD workflow
- Generated: By Claude Code analysis
- Committed: Yes

**`docs/`:**
- Purpose: Project documentation
- Generated: No
- Committed: Yes (on `test/unit-tests-coverage` branch, not yet on `main`)

---

*Structure analysis: 2026-04-03*
