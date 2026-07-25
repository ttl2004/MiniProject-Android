# Fix Resources$NotFoundException in CategoryAdapter

The application crashes with a `Resources$NotFoundException: Resource ID #0x0` because `CategoryAdapter` attempts to load a color resource using an identifier that doesn't exist (returning 0), which is then passed to `ContextCompat.getColor()`.

## User Review Required

> [!IMPORTANT]
> The `DatabaseSeeder` currently uses hex color strings (e.g., `#F8CACA`) instead of resource names. I will update `CategoryAdapter` to handle both hex strings and resource names to be more robust.

## Proposed Changes

### [Component Name] UI Adapters

#### [MODIFY] [CategoryAdapter.java](file:///E:/MyApplication/app/src/main/java/com/example/myapplication/adapter/CategoryAdapter.java)
- Add `import android.graphics.Color;`
- Update `onBindViewHolder` to safely handle color loading.
- Check if the color string is a hex value or a resource name.
- Provide a fallback color to prevent crashes if the resource is not found.

### [Component Name] Data Seeding

#### [MODIFY] [DatabaseSeeder.java](file:///E:/MyApplication/app/src/main/java/com/example/myapplication/data/DatabaseSeeder.java)
- Update `icons` array to use correct resource names (changing `icon_category_` prefix to `ic_category_`).

## Verification Plan

### Automated Tests
- Build the project to ensure no syntax errors.

### Manual Verification
- Run the application and navigate to the Category screen to verify that categories are displayed with their correct icons and background colors without crashing.
