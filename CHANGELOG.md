# Change Log
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/)
and this project adheres to [Semantic Versioning](http://semver.org/).

## Unreleased
### Added
- Language filter with toggle button
- Show preferences directory in "About" dialog

### Fixed
- Save file in utf-8 format

## [2.3.1] - 2018-01-06
### Changed
- Added border to QuickSearch dialog

### Fixed
- Fixed syntax highlight for php and html

## [2.3.0] - 2017-11-05
### Added
- Added support to handle snippets in offline mode
- Added settings dialog with editor theme and font size option
- Added title and description to snippet view
- Added log4j.properties configuration
- Log error if can't open web browser
- Text can be dragged into table to create a new snippet
- Snippets can be dragged directly into editor and the code are copied
- Added "save as file" feature
- Added "clone snippet" feature
- Added "snippet properties" dialog
- Detect binary file

### Fixed
- Show a dialog error if verification code is not correct

## [2.2.0] - 2017-04-22
### Added
- Added key shortcut to get focus on filter snippets box
- Added welcome panel
- About dialog shows plugin version, framework version and data storage directory

## [2.1.0] - 2017-03-25
### Added
- New CHANGELOG.md file

### Changed
- Storage is based on internal db with [Ormlite](http://ormlite.com/) and [H2](http://www.h2database.com)

### Fixed
- Fixed logout on IDE restart

### Removed
- Removed IStorage

## 2.0.0 - 2016-07-11
### Added
- New user interface
- List of snippets stored locally
- Syntax highlight powered by <a href="http://bobbylight.github.io/RSyntaxTextArea/">RSyntaxTextArea</a>
- Snippets are synchronized with server
- Filter snippets
- Quick search feature
- Insert selected snippet at cursor in document 

### Changed
- Error messages with log and dialog

## 1.1.2 - 2014-10-03
### Added
- Console write also snippet title when new snippet is created (thanks to bejoy)

### Changed
- Switched authentication from OAuth 1.0a to OAuth 2

## 1.1.0 - 2014-08-19
### Added
- Added "Search snippets" feature
- Fixed some minor bugs

## 1.0.0 - 2014-04-14
### Added
- First release with feature "Create snippet"

[2.3.1]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.3.0...v2.3.1
[2.3.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.2.0...v2.3.0
[2.2.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.1.0...v2.2.0
[2.1.0]: https://github.com/massimozappino/tagmycode-java-plugin-framework/compare/v2.0.0...v2.1.0
